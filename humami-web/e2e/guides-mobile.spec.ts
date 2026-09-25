import { test, expect } from '@playwright/test';

const BASE_URL = process.env.E2E_BASE_URL || 'http://localhost:3000';
const GUIDE_PATH = '/guias/pizza-napolitana-desde-cero';

test.describe('guides on mobile', () => {
  test.use({ viewport: { width: 375, height: 812 } });

  test('keeps the landing and protected reader within the viewport', async ({ page }) => {
    await page.goto(`${BASE_URL}${GUIDE_PATH}`, { waitUntil: 'domcontentloaded' });

    await expect(page.getByRole('heading', { name: 'Tu primera pizza napolitana en casa' })).toBeVisible();
    await expect(page.getByRole('button', { name: 'Ver la guía ahora' })).toBeVisible();
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);

    await page.getByRole('textbox', { name: 'Email' }).fill('preview@example.com');
    await page.getByLabel(/He leído y acepto la política/).check();
    await page.getByRole('button', { name: 'Ver la guía ahora' }).click();

    await expect(page).toHaveURL(/\/guias\/pizza-napolitana-desde-cero\/leer/);
    await expect(page.getByTitle('Tu primera pizza napolitana en casa')).toBeVisible();
    expect(await page.evaluate(() => document.documentElement.scrollWidth <= window.innerWidth)).toBe(true);
  });
});
