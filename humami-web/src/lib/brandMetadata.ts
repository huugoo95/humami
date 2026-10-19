import type { Metadata } from "next";

export const BRAND_SOCIAL_IMAGE = "/og-humami-v2.jpg";

const brandSocialImage = {
  url: BRAND_SOCIAL_IMAGE,
  width: 1200,
  height: 630,
  type: "image/jpeg",
  alt: "Humami — Recetas y cocina con sabor auténtico",
};

export const mealsMetadata: Metadata = {
  title: "Recetas",
  description: "Descubre recetas para cocinar con sabor auténtico.",
  alternates: { canonical: "/meals" },
  openGraph: {
    type: "website",
    url: "/meals",
    title: "Recetas | Humami",
    description: "Descubre recetas para cocinar con sabor auténtico.",
    images: [brandSocialImage],
  },
  twitter: {
    card: "summary_large_image",
    title: "Recetas | Humami",
    description: "Descubre recetas para cocinar con sabor auténtico.",
    images: [BRAND_SOCIAL_IMAGE],
  },
};

type MealMetadataInput = {
  id: string;
  name: string;
  description?: string;
  image?: string;
};

export function mealMetadata(meal: MealMetadataInput): Metadata {
  const description = meal.description || "Descubre la receta de " + meal.name + " en Humami.";
  const url = "/meals/" + meal.id;
  const image = meal.image
    ? { url: meal.image, alt: meal.name }
    : { ...brandSocialImage, alt: meal.name + " | Humami" };

  return {
    title: meal.name,
    description,
    alternates: { canonical: url },
    openGraph: {
      type: "article",
      title: meal.name,
      description,
      url,
      images: [image],
    },
    twitter: {
      card: "summary_large_image",
      title: meal.name,
      description,
      images: [image.url],
    },
  };
}

type BlogPostMetadataInput = {
  slug: string;
  title: string;
  excerpt?: string;
  seoTitle?: string;
  seoDescription?: string;
  coverImage?: string;
};

export function blogPostMetadata(post: BlogPostMetadataInput): Metadata {
  const title = post.seoTitle || post.title;
  const description = post.seoDescription || post.excerpt || post.title + " en el blog de Humami.";
  const url = "/blog/" + post.slug;
  const image = post.coverImage
    ? { url: post.coverImage, alt: post.title }
    : { ...brandSocialImage, alt: post.title + " | Humami" };

  return {
    title,
    description,
    alternates: { canonical: url },
    openGraph: {
      type: "article",
      title,
      description,
      url,
      images: [image],
    },
    twitter: {
      card: "summary_large_image",
      title,
      description,
      images: [image.url],
    },
  };
}
