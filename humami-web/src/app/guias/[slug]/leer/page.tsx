import { notFound } from "next/navigation";
import { GuideReader } from "@/features/guides/GuideReader";
import { getPublishedGuide } from "@/features/guides/api";

type Props = { params: Promise<{ slug: string }> };

export default async function GuideReaderPage({ params }: Props) {
  const { slug } = await params;
  const guide = await getPublishedGuide(slug);
  if (!guide) notFound();
  return <GuideReader slug={guide.slug} title={guide.title} />;
}
