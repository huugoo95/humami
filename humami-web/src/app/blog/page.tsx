import Link from "next/link";
import apiClient from "@/config/api";
import { BlogPost } from "@/types/blog";

async function getPublishedPosts(): Promise<BlogPost[]> {
  try {
    const response = await apiClient.get("/blog");
    return response.data;
  } catch (error) {
    console.error("Error loading blog posts", error);
    return [];
  }
}

export default async function BlogIndexPage() {
  const posts = await getPublishedPosts();

  return (
    <div className="max-w-5xl mx-auto p-6">
      <h1 className="text-4xl font-bold mb-6">Blog Humami</h1>

      {posts.length === 0 ? (
        <p className="text-gray-600">Aún no hay artículos publicados.</p>
      ) : (
        <div className="space-y-4">
          {posts.map((post) => (
            <article key={post.id} className="border rounded-lg p-4 shadow-sm bg-white">
              <Link href={`/blog/${post.slug}`}>
                <h2 className="text-2xl font-semibold hover:underline">{post.title}</h2>
              </Link>
              {post.excerpt && <p className="text-gray-700 mt-2">{post.excerpt}</p>}
              <div className="text-sm text-gray-500 mt-3">
                {post.author && <span>Por {post.author}</span>}
                {post.publishedAt && (
                  <span className="ml-3">
                    {new Date(post.publishedAt).toLocaleDateString("es-ES")}
                  </span>
                )}
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  );
}
