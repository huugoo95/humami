export interface Ingredient {
    name: string;
    quantity: number;
    unit: string;
  }
  
  export interface Recipe {
    id: string | null;
    name: string;
    description: string;
    instructions: string[];
    ingredients: Ingredient[];
    prepTime: number | null;
    difficulty: string;
    portions: number;
  }
  
  export interface Meal {
    id: string;
    name: string;
    description: string;
    image: string;
    difficulty?: 'fácil' | 'media' | 'difícil';
    type?: string;
    servings?: number;
    recipes: Recipe[];
    faqs?: {
    question: string;
    answer: string;
  }[];
  }