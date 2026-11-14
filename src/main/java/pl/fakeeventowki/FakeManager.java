package pl.fakeeventowki;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Trzyma listę slotów, w których przedmiot jest podmieniony na "eventówkę".
 * Serwer co jakiś czas odsyła prawdziwy ekwipunek, więc co tick podmiana jest
 * przywracana, dopóki w slocie leży ten sam typ przedmiotu co na początku.
 */
public final class FakeManager {
	private record Entry(FakeEvent event, ItemStack original) {}

	private static final Map<Integer, Entry> ENTRIES = new HashMap<>();

	private FakeManager() {}

	/** Podmienia przedmiot z aktualnie wybranego slotu hotbara. */
	public static void apply(FakeEvent event) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		PlayerInventory inv = player.getInventory();
		int slot = inv.getSelectedSlot();
		ItemStack current = inv.getStack(slot);

		if (current.isEmpty()) {
			player.sendMessage(Text.literal("Weź coś do ręki (np. dirt), żeby to zamienić!")
				.formatted(Formatting.RED), true);
			return;
		}

		ItemStack original = FakeEvent.isFake(current)
			? (ENTRIES.containsKey(slot) ? ENTRIES.get(slot).original() : current.copy())
			: current.copy();

		ENTRIES.put(slot, new Entry(event, original));
		inv.setStack(slot, event.create(current.getCount()));
		player.sendMessage(Text.literal("Ustawiono: ").formatted(Formatting.GRAY)
			.append(event.displayName()), true);
	}

	/** Przywraca oryginalny przedmiot w wybranym slocie. */
	public static void reset() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;

		PlayerInventory inv = player.getInventory();
		int slot = inv.getSelectedSlot();
		Entry entry = ENTRIES.remove(slot);
		ItemStack current = inv.getStack(slot);

		if (entry != null && FakeEvent.isFake(current)) {
			ItemStack restored = entry.original().copy();
			restored.setCount(Math.max(1, Math.min(current.getCount(), restored.getMaxCount())));
			inv.setStack(slot, restored);
		}
		player.sendMessage(Text.literal("Przywrócono oryginalny przedmiot.")
			.formatted(Formatting.GRAY), true);
	}

	public static void resetAll() {
		ENTRIES.clear();
	}

	public static void tick(MinecraftClient client) {
		ClientPlayerEntity player = client.player;
		if (player == null) {
			ENTRIES.clear();
			return;
		}
		if (ENTRIES.isEmpty()) return;

		PlayerInventory inv = player.getInventory();
		Iterator<Map.Entry<Integer, Entry>> it = ENTRIES.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Entry> e = it.next();
			int slot = e.getKey();
			Entry entry = e.getValue();
			ItemStack stack = inv.getStack(slot);

			if (FakeEvent.isFake(stack)) continue;

			if (stack.isEmpty() || stack.getItem() != entry.original().getItem()) {
				// gracz wyrzucił / zużył / podmienił przedmiot - przestajemy śledzić slot
				it.remove();
				continue;
			}
			inv.setStack(slot, entry.event().create(stack.getCount()));
		}
	}
}
