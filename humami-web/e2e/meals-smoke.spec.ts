import { test, expect } from '@playwright/test';

const BASE_URL = process.env.E2E_BASE_URL || 'https://humami.es';

test('meals page loads and shows recipe entries', async ({ page }) => {
  await page.goto(`${BASE_URL}/meals`, { waitUntil: 'domcontentloaded' });

  await expect(page.getByRole('heading', { name: /buscar recetas/i })).toBeVisible();

  const cards = page.locator('a[href^="/meals/"]');
  await expect(cards.first()).toBeVisible();

  await cards.first().click();
  await expect(page).toHaveURL(/\/meals\//);
});
