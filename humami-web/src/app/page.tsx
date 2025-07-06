import Link from "next/link";

export default function Home() {
  return (
    <main
      className={`
        relative min-h-screen flex flex-col items-center justify-center
        px-6 py-12   
        text-humami-text-base 
      `}
    >
      <div className="relative z-10 text-center max-w-3xl space-y-6">
        <h1 className="text-4xl md:text-6xl font-heading text-humami-text-heading">
          Redescubre la gastronomía
        </h1>
        <p className="text-lg md:text-xl font-body">
          Un viaje culinario que transforma lo cotidiano en memorable.
        </p>
        <Link
          href="/meals"
          className={`
            inline-block px-6 py-3 font-medium rounded-md
            bg-humami-accent-dark
            text-humami-bg
            transition hover:bg-humami-accent-dark
          `}
        >
          Explorar comidas
        </Link>
      </div>
    </main>
  );
}