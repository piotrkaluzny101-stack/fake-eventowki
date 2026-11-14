package pl.fakeeventowki;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public enum FakeEvent {
	// ===== Eventówki Stałe =====
	TOTEM(Category.STALE, "&4&lTotem Ułaskawienia", Items.TOTEM_OF_UNDYING,
		"&7Po śmierci w grze nie tracisz",
		"&7swoich przedmiotów z ekwipunku.",
		"&7Przedmiot zostaje zużyty przy aktywacji."),
	KORONA(Category.STALE, "&6&lKorona Anarchii", Items.GOLDEN_HELMET,
		"&7Założona na głowę zapewnia stałe efekty:",
		"&eSzybkość II, Odporność na ogień I,",
		"&eSiła II, Odporność III oraz Szczęście I."),
	EKSKALIBUR(Category.STALE, "&c&lEkskalibur", Items.NETHERITE_SWORD,
		"&7Legendarny miecz, który zadaje",
		"&7potężne obrażenia i podpala cel."),
	TOPOR(Category.STALE, "&c&lBoski Topór", Items.NETHERITE_AXE,
		"&7Kliknij Prawym Przyciskiem Myszy,",
		"&7aby odepchnąć wszystkich graczy wokół",
		"&7i zyskać efekt nieśmiertelności na 3 sekundy."),

	// ===== Eventówki Zużywalne =====
	BOMBARDA(Category.ZUZYWALNE, "&d&lBombarda Maxima", Items.FIRE_CHARGE,
		"&7Po wystrzeleniu wybucha w okolicy",
		"&7i niszczy każdy możliwy blok",
		"&7oprócz skały macierzystej (bedrocka)."),
	KREW(Category.ZUZYWALNE, "&4&lKrew Wampira", Items.REDSTONE,
		"&7Kliknij Prawym Przyciskiem Myszy,",
		"&7aby natychmiastowo przywrócić",
		"&7pełny pasek zdrowia (HP)."),
	RUBIK(Category.ZUZYWALNE, "&5&lKostka Rubika", Items.SLIME_BLOCK,
		"&7Uderz przeciwnika, aby całkowicie",
		"&7wymieszać ułożenie przedmiotów",
		"&7w jego ekwipunku."),
	SNIEZKA(Category.ZUZYWALNE, "&a&lŚnieżka Zamiany Miejsc", Items.SNOWBALL,
		"&7Trafienie wroga tą śnieżką",
		"&7powoduje natychmiastową zamianę",
		"&7waszych pozycji na mapie.");

	public enum Category { STALE, ZUZYWALNE }

	public static final String MARKER = "fake_event";

	public final Category category;
	public final String rawName;
	public final Item item;
	public final String[] rawLore;

	FakeEvent(Category category, String rawName, Item item, String... rawLore) {
		this.category = category;
		this.rawName = rawName;
		this.item = item;
		this.rawLore = rawLore;
	}

	public MutableText displayName() {
		return TextUtil.parse(rawName);
	}

	public Text loreText() {
		MutableText out = displayName();
		for (String line : rawLore) {
			out.append(Text.literal("\n"));
			out.append(TextUtil.parse(line));
		}
		return out;
	}

	/** Buduje wizualną kopię przedmiotu (tylko po stronie klienta). */
	public ItemStack create(int count) {
		ItemStack stack = new ItemStack(item, 1);
		stack.setCount(Math.max(1, Math.min(count, stack.getMaxCount())));

		stack.set(DataComponentTypes.CUSTOM_NAME, displayName());

		List<Text> lore = new ArrayList<>();
		for (String line : rawLore) {
			lore.add(TextUtil.parse(line));
		}
		stack.set(DataComponentTypes.LORE, new LoreComponent(lore));
		stack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);

		NbtCompound tag = new NbtCompound();
		tag.putBoolean(MARKER, true);
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
		return stack;
	}

	public static boolean isFake(ItemStack stack) {
		if (stack.isEmpty()) return false;
		NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
		return data != null && data.copyNbt().contains(MARKER);
	}
}
