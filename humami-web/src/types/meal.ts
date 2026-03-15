export interface Ingredient {
    name: string;
    quantity: number;
    unit: string;
    isOptional?: boolean;
  }

  export interface InstructionStep {
    order: number;
    text: string;
  }
  
  export interface Recipe {
    id: string | null;
    name: string;
    description: string;
    instructions?: string[]; // legacy
    instructionSteps?: InstructionStep[];
    ingredients: Ingredient[];
    prepTime: number | null;
    difficulty: string;
    portions: number;
  }

  export interface RecipeIngredientsGroup {
    recipeName: string;
    recipeDescription?: string;
    ingredients: Ingredient[];
  }
  
  export interface Meal {
    id: string;
    name: string;
    description: string;
    image: string;
    difficulty?: 'fácil' | 'media' | 'difícil';
    type?: string;
    servings?: number;
    ingredientsByRecipe?: RecipeIngredientsGroup[];
    recipes: Recipe[];
    faqs?: {
    question: string;
    answer: string;
  }[];
  }