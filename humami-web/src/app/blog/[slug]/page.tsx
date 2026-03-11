import apiClient from "@/config/api";
import { BlogPost } from "@/types/blog";
import { notFound } from "next/navigation";
import Image from "next/image";

async function getPost(slug: string): Promise<BlogPost | null> {
  try {
    const response = await apiClient.get(`/blog/${slug}`);
    return response.data;
  } catch {
    return null;
  }
}

export default async function BlogPostPage({ params }: { params: { slug: string } }) {
  const post = await getPost(params.slug);

  if (!post) return notFound();

  return (
    <article className="max-w-4xl mx-auto p-6">
      <h1 className="text-4xl font-bold mb-4">{post.title}</h1>

      <div className="text-sm text-gray-500 mb-6">
        {post.author && <span>Por {post.author}</span>}
        {post.publishedAt && (
          <span className="ml-3">{new Date(post.publishedAt).toLocaleDateString("es-ES")}</span>
        )}
      </div>

      {post.coverImage && (
        <div className="mb-6">
          <Image
            src={post.coverImage}
            alt={post.title}
            width={1200}
            height={700}
            className="rounded-lg w-full h-auto"
          />
        </div>
      )}

      {post.excerpt && <p className="text-lg text-gray-700 mb-6">{post.excerpt}</p>}

      <div className="prose prose-lg max-w-none whitespace-pre-wrap">{post.content}</div>
    </article>
  );
}
