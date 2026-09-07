import { useState } from 'react';
import CadastroItem from './telas/CadastroItem';
import ListaItens from './telas/ListaItens';

const ABAS = [
  { id: 'lista', titulo: 'Catálogo', tela: <ListaItens /> },
  { id: 'cadastro', titulo: 'Cadastrar Item', tela: <CadastroItem /> },
];

function App() {
  const [abaAtiva, setAbaAtiva] = useState('lista');

  return (
    <div style={{
      backgroundColor: '#f3f4f6',
      minHeight: '100vh',
      padding: '40px 20px',
    }}>
      <nav style={{
        display: 'flex', justifyContent: 'center', gap: '10px',
        marginBottom: '30px', fontFamily: 'sans-serif',
      }}>
        {ABAS.map((aba) => (
          <button
            key={aba.id}
            onClick={() => setAbaAtiva(aba.id)}
            style={{
              padding: '10px 20px',
              borderRadius: '8px',
              border: 'none',
              fontWeight: 'bold',
              fontSize: '15px',
              cursor: 'pointer',
              backgroundColor: abaAtiva === aba.id ? '#1d4ed8' : 'white',
              color: abaAtiva === aba.id ? 'white' : '#374151',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            }}
          >
            {aba.titulo}
          </button>
        ))}
      </nav>

      {ABAS.find((aba) => aba.id === abaAtiva).tela}
    </div>
  );
}

export default App;
