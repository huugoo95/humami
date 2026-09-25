import type { MetadataRoute } from "next";

type MealsPageResponse = {
  items: { id: string }[];
  page: number;
  totalPages: number;
};

type GuideResponse = {
  slug: string;
  publishedAt?: string;
};

const BASE_URL = "https://humami.es";

async function fetchMealUrls(): Promise<MetadataRoute.Sitemap> {
  const urls: MetadataRoute.Sitemap = [];
  const limit = 100;

  try {
    const firstRes = await fetch(`${BASE_URL}/api/meals?query=&page=1&limit=${limit}`, {
      next: { revalidate: 3600 },
    });

    if (!firstRes.ok) return urls;

    const firstPage = (await firstRes.json()) as MealsPageResponse;

    const collectPage = (payload: MealsPageResponse) => {
      (payload.items || []).forEach((meal) => {
        if (meal?.id) {
          urls.push({
            url: `${BASE_URL}/meals/${meal.id}`,
            changeFrequency: "weekly",
            priority: 0.7,
          });
        }
      });
    };

    collectPage(firstPage);

    const totalPages = Math.max(firstPage.totalPages || 1, 1);

    for (let page = 2; page <= totalPages; page++) {
      const res = await fetch(`${BASE_URL}/api/meals?query=&page=${page}&limit=${limit}`, {
        next: { revalidate: 3600 },
      });
      if (!res.ok) continue;
      const payload = (await res.json()) as MealsPageResponse;
      collectPage(payload);
    }
  } catch {
    // keep static sitemap entries if meals fetch fails
  }

  return urls;
}

async function fetchGuideUrls(): Promise<MetadataRoute.Sitemap> {
  try {
    const response = await fetch(`${BASE_URL}/api/guides`, { next: { revalidate: 3600 } });
    if (!response.ok) return [];
    const guides = (await response.json()) as GuideResponse[];
    return guides.filter((guide) => guide?.slug).map((guide) => ({
      url: `${BASE_URL}/guias/${guide.slug}`,
      lastModified: guide.publishedAt ? new Date(guide.publishedAt) : new Date(),
      changeFrequency: "monthly" as const,
      priority: 0.7,
    }));
  } catch {
    return [];
  }
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const now = new Date();

  const staticUrls: MetadataRoute.Sitemap = [
    {
      url: `${BASE_URL}/`,
      lastModified: now,
      changeFrequency: "daily",
      priority: 1,
    },
    {
      url: `${BASE_URL}/meals`,
      lastModified: now,
      changeFrequency: "daily",
      priority: 0.9,
    },
    {
      url: `${BASE_URL}/our-story`,
      lastModified: now,
      changeFrequency: "monthly",
      priority: 0.6,
    },
    {
      url: `${BASE_URL}/blog`,
      lastModified: now,
      changeFrequency: "weekly",
      priority: 0.6,
    },
    {
      url: `${BASE_URL}/guias`,
      lastModified: now,
      changeFrequency: "weekly",
      priority: 0.7,
    },
  ];

  const [mealUrls, guideUrls] = await Promise.all([fetchMealUrls(), fetchGuideUrls()]);

  return [...staticUrls, ...mealUrls, ...guideUrls];
}
