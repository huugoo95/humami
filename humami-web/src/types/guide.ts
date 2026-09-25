export type Guide = {
  id: string;
  slug: string;
  title: string;
  excerpt: string;
  landingHeadline?: string;
  landingDescription?: string;
  coverImage?: string;
  previewImage?: string;
  duration?: string;
  level?: string;
  pageCount?: number;
  contents?: string[];
  status?: string;
  seoTitle?: string;
  seoDescription?: string;
  publishedAt?: string;
};

export type GuideAccessRequest = {
  email: string;
  privacyNoticeVersion: string;
  privacyAccepted: boolean;
  marketingOptIn: boolean;
  source: string;
  website?: string;
};

export type GuideAccessResponse = {
  readerUrl: string;
};

export type GuideDocumentResponse = {
  documentUrl: string;
};
