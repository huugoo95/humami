import "@/styles/globals.css";
import { Inter } from "next/font/google";
import { cn } from "@/lib/utils";
import Link from 'next/link'

const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="es">
      <body
        className={cn(
          inter.className,
          "bg-green-900 text-gray-100 min-h-screen flex flex-col"
        )}
      >
        <header className="bg-burgundy-800 text-white p-4 shadow-lg">
          <div className="container mx-auto flex justify-between items-center">
            <h1 className="text-2xl font-semibold tracking-wide">Humami</h1>
            <nav>
              <ul className="flex space-x-6">
                <li>
                  <Link href="/" className="hover:text-gray-300 transition-colors">
                    Inicio
                  </Link>
                </li>
                <li>
                <Link href="/meals" className="hover:text-gray-300 transition-colors">
                  Comidas
                </Link>
                </li>
              </ul>
            </nav>
          </div>
        </header>
        <main className="container mx-auto flex-1 p-6">{children}</main>
        <footer className="bg-burgundy-900 text-center p-4 text-gray-300 mt-8">
          &copy; {new Date().getFullYear()} Humami - Todos los derechos reservados.
        </footer>
      </body>
    </html>
  );
}
