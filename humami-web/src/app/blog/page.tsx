import Link from "next/link";
import { API_BASE_URL } from "@/config/api";
import { BlogPost } from "@/types/blog";

async function getPublishedPosts(): Promise<BlogPost[]> {
  if (!API_BASE_URL) {
    return [];
  }

  try {
    const response = await fetch(API_BASE_URL + "/blog", { cache: "no-store" });

    if (!response.ok) {
      return [];
    }

    return (await response.json()) as BlogPost[];
  } catch (error) {
    console.error("Error loading blog posts", error);
    return [];
  }
}

export default async function BlogIndexPage() {
  const posts = await getPublishedPosts();

  return (
    <section className="mx-auto max-w-5xl">
      <p className="mb-3 text-sm font-semibold uppercase tracking-wider text-humami-accent-dark">Humami</p>
      <h1 className="mb-6 font-heading text-4xl text-humami-text-heading sm:text-5xl">Blog</h1>

      {posts.length === 0 ? (
        <p className="rounded-xl border border-humami-text-base/10 bg-white/70 p-6 text-humami-text-base">
          Aún no hay artículos publicados.
        </p>
      ) : (
        <div className="space-y-4">
          {posts.map((post) => (
            <article key={post.id} className="rounded-xl border border-humami-text-base/10 bg-white/70 p-6 shadow-sm">
              <Link href={"/blog/" + post.slug}>
                <h2 className="font-heading text-3xl text-humami-accent-dark hover:underline">{post.title}</h2>
              </Link>
              {post.excerpt && <p className="mt-3 leading-relaxed text-humami-text-base">{post.excerpt}</p>}
              <div className="mt-4 text-sm text-humami-text-base/70">
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
    </section>
  );
}
