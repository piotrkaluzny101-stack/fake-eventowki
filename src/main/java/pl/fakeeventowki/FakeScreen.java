package pl.fakeeventowki;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FakeScreen extends Screen {
	private static final int BTN_W = 190;
	private static final int BTN_H = 20;
	private static final int GAP = 4;

	public FakeScreen() {
		super(Text.literal("Fake Eventówki"));
	}

	@Override
	protected void init() {
		int leftX = this.width / 2 - BTN_W - 6;
		int rightX = this.width / 2 + 6;
		int startY = 56;

		int left = 0;
		int right = 0;
		for (FakeEvent event : FakeEvent.values()) {
			boolean stale = event.category == FakeEvent.Category.STALE;
			int x = stale ? leftX : rightX;
			int y = startY + (stale ? left++ : right++) * (BTN_H + GAP);

			this.addDrawableChild(ButtonWidget.builder(event.displayName(), btn -> {
					FakeManager.apply(event);
					this.close();
				})
				.dimensions(x, y, BTN_W, BTN_H)
				.tooltip(Tooltip.of(event.loreText()))
				.build());
		}

		int bottomY = startY + 4 * (BTN_H + GAP) + 10;
		this.addDrawableChild(ButtonWidget.builder(
				Text.literal("Przywróć oryginalny przedmiot").formatted(Formatting.YELLOW),
				btn -> {
					FakeManager.reset();
					this.close();
				})
			.dimensions(this.width / 2 - BTN_W - 6, bottomY, BTN_W, BTN_H)
			.build());
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), btn -> this.close())
			.dimensions(this.width / 2 + 6, bottomY, BTN_W, BTN_H)
			.build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredTextWithShadow(this.textRenderer,
			Text.literal("Fake Eventówki").formatted(Formatting.GOLD, Formatting.BOLD),
			this.width / 2, 14, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer,
			Text.literal("Weź przedmiot do ręki i kliknij nazwę eventówki").formatted(Formatting.GRAY),
			this.width / 2, 28, 0xFFFFFF);

		int leftX = this.width / 2 - BTN_W - 6;
		int rightX = this.width / 2 + 6;
		context.drawCenteredTextWithShadow(this.textRenderer,
			Text.literal("Eventówki Stałe").formatted(Formatting.GOLD),
			leftX + BTN_W / 2, 43, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer,
			Text.literal("Eventówki Zużywalne").formatted(Formatting.LIGHT_PURPLE),
			rightX + BTN_W / 2, 43, 0xFFFFFF);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
