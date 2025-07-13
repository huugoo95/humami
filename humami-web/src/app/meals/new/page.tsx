'use client';

import React, { useState, FormEvent } from 'react';
import { useRouter } from 'next/navigation';
import apiClient from '@/config/api';

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

const initialRecipe: Recipe = {
  name: '',
  description: '',
  instructions: [''],
  ingredients: [{ name: '', quantity: 0, unit: '', link: '' }],
  prepTime: 0,
  difficulty: 'Fácil',
  portions: 1,
};

export default function CreateMealPage() {
  const router = useRouter();
  const [name, setName] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [recipes, setRecipes] = useState<Recipe[]>([initialRecipe]);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const handleAddRecipe = () => {
    setRecipes(prev => [...prev, initialRecipe]);
  };

  function handleRecipeChange<K extends keyof Recipe>(
    rIdx: number,
    field: K,
    value: Recipe[K]
  ) {
    setRecipes(prev => {
      const copy = [...prev];
      copy[rIdx] = { ...copy[rIdx], [field]: value };
      return copy;
    });
  }

  function handleInstructionChange(
    rIdx: number,
    iIdx: number,
    value: string
  ) {
    setRecipes(prev => {
      const copy = [...prev];
      const ins = [...copy[rIdx].instructions];
      ins[iIdx] = value;
      copy[rIdx] = { ...copy[rIdx], instructions: ins };
      return copy;
    });
  }

  const addInstruction = (rIdx: number) => {
    setRecipes(prev => {
      const copy = [...prev];
      copy[rIdx] = {
        ...copy[rIdx],
        instructions: [...copy[rIdx].instructions, ''],
      };
      return copy;
    });
  };

  function handleIngredientChange<K extends keyof Ingredient>(
    rIdx: number,
    iIdx: number,
    field: K,
    value: Ingredient[K]
  ) {
    setRecipes(prev => {
      const copy = [...prev];
      const ings = [...copy[rIdx].ingredients];
      ings[iIdx] = { ...ings[iIdx], [field]: value };
      copy[rIdx] = { ...copy[rIdx], ingredients: ings };
      return copy;
    });
  }

  const addIngredient = (rIdx: number) => {
    setRecipes(prev => {
      const copy = [...prev];
      copy[rIdx] = {
        ...copy[rIdx],
        ingredients: [...copy[rIdx].ingredients, { name: '', quantity: 0, unit: '', link: '' }],
      };
      return copy;
    });
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
  e.preventDefault();
  if (!imageFile) return alert('Debes subir una imagen');
  setSubmitting(true);

  const mealRequest = { name, description, recipes };
  const formData = new FormData();
  formData.append('mealRequest', new Blob([JSON.stringify(mealRequest)], { type: 'application/json' }));
  formData.append('image', imageFile);

  // *** Debug: imprimimos las entries de FormData ***
  for (const [key, val] of formData.entries()) {
    console.log('▶️ formData entry:', key, val);
  }

  try {
    const { data: created } = await apiClient.post<MealResponse>('/meals', formData, {
      headers: { /* no Content-Type aquí */ },
    });
    router.push(`/meals/${created.id}`);
  } catch (err: any) {
    console.error(err);
    alert('falllaaaaaa: ' + err.message);
  } finally {
    setSubmitting(false);
  }
};

  return (
    <div className="bg-humami-bg-light min-h-screen py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto bg-humami-bg shadow-xl rounded-2xl p-8">
        <h1 className="text-3xl sm:text-4xl font-bold text-humami-text-heading mb-8">
          Crear nuevo Menú
        </h1>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Nombre */}
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-humami-text-base mb-2">
              Nombre del menú
            </label>
            <input
              id="name"
              type="text"
              value={name}
              onChange={e => setName(e.target.value)}
              required
              className="block w-full border border-gray-200 bg-white rounded-lg px-4 py-2 text-humami-text-base placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-humami-accent transition"
            />
          </div>

          {/* Descripción */}
          <div>
            <label htmlFor="description" className="block text-sm font-medium text-humami-text-base mb-2">
              Descripción
            </label>
            <textarea
              id="description"
              value={description}
              onChange={e => setDescription(e.target.value)}
              required
              rows={3}
              className="block w-full border border-gray-200 bg-white rounded-lg px-4 py-2 text-humami-text-base placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-humami-accent transition"
            />
          </div>

          {/* Imagen */}
          <div>
            <label htmlFor="image" className="block text-sm font-medium text-humami-text-base mb-2">
              Imagen del menú
            </label>
            <input
              id="image"
              type="file"
              accept="image/*"
              onChange={e => setImageFile(e.target.files?.[0] ?? null)}
              required
              className="block w-full text-sm text-humami-text-base file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:bg-humami-accent file:text-white hover:file:bg-humami-accent-dark transition"
            />
          </div>

          {/* Recetas */}
          <div>
            <h2 className="text-2xl font-semibold text-humami-text-heading mb-4">Recetas</h2>
            {recipes.map((r, idx) => (
              <div key={idx} className="border border-gray-200 bg-humami-bg-light rounded-lg p-6 mb-6">
                {/* Nombre receta */}
                <div className="mb-4">
                  <label className="block text-sm font-medium text-humami-text-base mb-1">
                    Nombre de la receta
                  </label>
                  <input
                    type="text"
                    value={r.name}
                    onChange={e => handleRecipeChange(idx, 'name', e.target.value)}
                    className="block w-full border border-gray-200 bg-white rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent transition"
                  />
                </div>

                {/* Descripción receta */}
                <div className="mb-4">
                  <label className="block text-sm font-medium text-humami-text-base mb-1">
                    Descripción
                  </label>
                  <textarea
                    value={r.description}
                    onChange={e => handleRecipeChange(idx, 'description', e.target.value)}
                    rows={2}
                    className="block w-full border border-gray-200 bg-white rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent transition"
                  />
                </div>

                {/* Tiempo, dificultad y porciones */}
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4">
                  <input
                    type="number"
                    value={r.prepTime}
                    onChange={e => handleRecipeChange(idx, 'prepTime', +e.target.value)}
                    placeholder="Min prep"
                    className="border border-gray-200 rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                  />
                  <select
                    value={r.difficulty}
                    onChange={e => handleRecipeChange(idx, 'difficulty', e.target.value as Difficulty)}
                    className="border border-gray-200 rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                  >
                    <option>Fácil</option>
                    <option>Media</option>
                    <option>Difícil</option>
                  </select>
                  <input
                    type="number"
                    value={r.portions}
                    onChange={e => handleRecipeChange(idx, 'portions', +e.target.value)}
                    placeholder="Porciones"
                    className="border border-gray-200 rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                  />
                </div>

                {/* Instrucciones */}
                <div className="mb-4">
                  <label className="block text-sm font-medium text-humami-text-base mb-2">
                    Instrucciones
                  </label>
                  <div className="space-y-2">
                    {r.instructions.map((ins, i) => (
                      <input
                        key={i}
                        type="text"
                        value={ins}
                        onChange={e => handleInstructionChange(idx, i, e.target.value)}
                        placeholder={`Paso ${i + 1}`}
                        className="w-full border border-gray-200 rounded-md px-3 py-2 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                      />
                    ))}
                  </div>
                  <button
                    type="button"
                    onClick={() => addInstruction(idx)}
                    className="mt-2 text-sm font-medium text-humami-accent hover:text-humami-accent-dark transition"
                  >
                    + Añadir instrucción
                  </button>
                </div>

                {/* Ingredientes */}
                <div>
                  <label className="block text-sm font-medium text-humami-text-base mb-2">
                    Ingredientes
                  </label>
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
                    {r.ingredients.map((ing, i) => (
                      <div key={i} className="space-y-1">
                        <input
                          type="text"
                          placeholder="Nombre"
                          value={ing.name}
                          onChange={e => handleIngredientChange(idx, i, 'name', e.target.value)}
                          className="w-full border border-gray-200 rounded-md px-2 py-1 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                        />
                        <input
                          type="number"
                          placeholder="Cant."
                          value={ing.quantity}
                          onChange={e => handleIngredientChange(idx, i, 'quantity', +e.target.value)}
                          className="w-full border border-gray-200 rounded-md px-2 py-1 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                        />
                        <input
                          type="text"
                          placeholder="Unidad"
                          value={ing.unit}
                          onChange={e => handleIngredientChange(idx, i, 'unit', e.target.value)}
                          className="w-full border border-gray-200 rounded-md px-2 py-1 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                        />
                        <input
                          type="text"
                          placeholder="Link"
                          value={ing.link}
                          onChange={e => handleIngredientChange(idx, i, 'link', e.target.value)}
                          className="w-full border border-gray-200 rounded-md px-2 py-1 text-humami-text-base focus:outline-none focus:ring-2 focus:ring-humami-accent"
                        />
                      </div>
                    ))}
                  </div>
                  <button
                    type="button"
                    onClick={() => addIngredient(idx)}
                    className="mt-2 text-sm font-medium text-humami-accent hover:text-humami-accent-dark transition"
                  >
                    + Añadir ingrediente
                  </button>
                </div>
              </div>
            ))}

            {/* Añadir otra receta */}
            <button
              type="button"
              onClick={handleAddRecipe}
              className="inline-flex items-center text-sm font-medium text-humami-accent hover:text-humami-accent-dark transition"
            >
              + Añadir otra receta
            </button>
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={submitting}
            className="w-full inline-flex justify-center py-3 px-6 bg-humami-accent text-white font-semibold rounded-lg hover:bg-humami-accent-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-humami-accent transition disabled:opacity-50"
          >
            {submitting ? 'Creando...' : 'Crear Menú'}
          </button>
        </form>
      </div>
    </div>
  );
}