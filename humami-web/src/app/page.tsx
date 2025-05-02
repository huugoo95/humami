import Link from 'next/link'

export default function Home() {
  return (
    <main className="relative min-h-screen text-white flex flex-col items-center justify-center px-6 py-12 bg-green-950">
      {/* Imagen de fondo con opacidad */}
      <div className="absolute inset-0 z-0">
        <img
          src="/linen.jpg" // Puedes cambiar esto por cualquier imagen sutil que subas a /public
          alt=""
          className="w-full h-full object-cover opacity-20"
        />
      </div>

      {/* Contenido sobre la imagen */}
      <div className="relative z-10 text-center px-4 max-w-4xl mx-auto">
        <h1 className="text-4xl md:text-6xl font-serif mb-6">
          Cocina natural, artesanal y con propósito
        </h1>
        <p className="text-lg md:text-xl max-w-xl mx-auto mb-8 text-green-100">
          Explora menús completos, pensados con ingredientes reales y técnicas de siempre.
        </p>
        <Link
          href="/meals"
          className="bg-white text-green-900 px-6 py-3 rounded-md font-medium transition duration-300 ease-in-out transform hover:bg-green-100 hover:scale-105"
        >
          Explorar comidas
        </Link>
      </div>
    </main>
  )
}