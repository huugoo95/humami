import { API_BASE_URL } from "@/config/api";
import { BlogPost } from "@/types/blog";
import { notFound } from "next/navigation";
import Image from "next/image";

async function getPost(slug: string): Promise<BlogPost | null> {
  if (!API_BASE_URL) {
    return null;
  }

  try {
    const response = await fetch(API_BASE_URL + "/blog/" + slug, { cache: "no-store" });

    if (!response.ok) {
      return null;
    }

    return (await response.json()) as BlogPost;
  } catch {
    return null;
  }
}

export default async function BlogPostPage({ params }: { params: { slug: string } }) {
  const post = await getPost(params.slug);

  if (!post) return notFound();

  return (
    <article className="mx-auto max-w-4xl rounded-2xl border border-humami-text-base/10 bg-white/70 p-6 shadow-sm md:p-10">
      <h1 className="mb-4 font-heading text-4xl text-humami-text-heading sm:text-5xl">{post.title}</h1>

      <div className="mb-6 text-sm text-humami-text-base/70">
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
            className="h-auto w-full rounded-xl border border-humami-text-base/10"
          />
        </div>
      )}

      {post.excerpt && <p className="mb-6 text-lg leading-relaxed text-humami-text-base">{post.excerpt}</p>}

      <div className="max-w-none whitespace-pre-wrap text-lg leading-relaxed text-humami-text-base">{post.content}</div>
    </article>
  );
}
