import { Meal } from '@/types/meal';

type ApiClientLike = {
  post: <T = unknown>(url: string, data?: unknown) => Promise<{ data: T }>;
  put: (url: string, data?: unknown) => Promise<unknown>;
};

type Ingredient = {
  name: string;
  quantity: number;
  unit: string;
  link: string;
};

type Difficulty = 'Fácil' | 'Media' | 'Difícil';

type Recipe = {
  name: string;
  description: string;
  instructions: string[];
  ingredients: Ingredient[];
  prepTime: number;
  difficulty: Difficulty;
  portions: number;
};

export async function submitMealWithImage(params: {
  apiClient: ApiClientLike;
  name: string;
  description: string;
  recipes: Recipe[];
  imageFile: File;
}): Promise<string> {
  const { apiClient, name, description, recipes, imageFile } = params;

  const mealRequest = { name, description, recipes };
  const { data: created } = await apiClient.post<Meal>('/meals', mealRequest);
  const mealId = created.id;

  const formData = new FormData();
  formData.append('image', imageFile);

  await apiClient.put(`/meals/${mealId}/image`, formData);

  return mealId;
}
