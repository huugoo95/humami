import { submitMealWithImage } from './submitMeal';

describe('submitMealWithImage', () => {
  it('creates meal first and then uploads image with created meal id', async () => {
    const post = jest.fn().mockResolvedValue({ data: { id: 'meal-123' } });
    const put = jest.fn().mockResolvedValue({});

    const imageFile = new File(['image-bytes'], 'meal.jpg', { type: 'image/jpeg' });

    const mealId = await submitMealWithImage({
      apiClient: { post, put },
      name: 'Menú test',
      description: 'Desc test',
      recipes: [
        {
          name: 'Receta',
          description: 'Desc receta',
          instructions: ['Paso 1'],
          ingredients: [{ name: 'Tomate', quantity: 1, unit: 'u', link: '' }],
          prepTime: 10,
          difficulty: 'Fácil',
          portions: 1,
        },
      ],
      imageFile,
    });

    expect(mealId).toBe('meal-123');
    expect(post).toHaveBeenCalledTimes(1);
    expect(post).toHaveBeenCalledWith('/meals', expect.objectContaining({
      name: 'Menú test',
      description: 'Desc test',
    }));

    expect(put).toHaveBeenCalledTimes(1);
    expect(put).toHaveBeenCalledWith('/meals/meal-123/image', expect.any(FormData));
  });

  it('does not upload image if meal creation fails', async () => {
    const post = jest.fn().mockRejectedValue(new Error('create failed'));
    const put = jest.fn();

    const imageFile = new File(['image-bytes'], 'meal.jpg', { type: 'image/jpeg' });

    await expect(
      submitMealWithImage({
        apiClient: { post, put },
        name: 'Menú test',
        description: 'Desc test',
        recipes: [],
        imageFile,
      })
    ).rejects.toThrow('create failed');

    expect(put).not.toHaveBeenCalled();
  });
});
