export type BlogPost = {
  id: string;
  slug: string;
  title: string;
  excerpt?: string;
  content: string;
  coverImage?: string;
  author?: string;
  tags?: string[];
  status?: string;
  seoTitle?: string;
  seoDescription?: string;
  publishedAt?: string;
  createdAt?: string;
  updatedAt?: string;
};
