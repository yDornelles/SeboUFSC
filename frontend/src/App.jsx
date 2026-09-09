import { useState, useEffect } from 'react';
import { onAuthStateChanged, signOut } from 'firebase/auth';
import { auth } from './firebase';

import CadastroItem from './telas/CadastroItem';
import ListaItens from './telas/ListaItens';
import LoginUsuario from './telas/LoginUsuario';
import CadastroUsuario from './telas/CadastroUsuario';

function App() {
  const [usuario, setUsuario] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState('lista');
  const [abaAuth, setAbaAuth] = useState('login'); // 'login' ou 'cadastro'

  // Monitora o estado da autenticação no Firebase
  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      setUsuario(user);
      setCarregando(false);
    });

    return () => unsubscribe();
  }, []);

  // Função para fazer logout
  const handleLogout = async () => {
    await signOut(auth);
  };

  // Renderiza a tela de acordo com a aba ativa, passando as props necessárias
  const renderizarTelaAtiva = () => {
    switch (abaAtiva) {
      case 'cadastro':
        return <CadastroItem usuario={usuario} />;
      case 'lista':
      default:
        return <ListaItens usuario={usuario} />;
    }
  };

  // Tela de carregamento simples enquanto verifica a sessão
  if (carregando) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', fontFamily: 'sans-serif' }}>
        <p>Carregando...</p>
      </div>
    );
  }

  // 1. SE O USUÁRIO NÃO ESTIVER LOGADO: Exibe telas de Login / Criar Conta
  if (!usuario) {
    return (
      <div style={{ backgroundColor: '#f3f4f6', minHeight: '100vh', padding: '40px 20px', fontFamily: 'sans-serif' }}>
        <nav style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginBottom: '30px' }}>
          <button
            onClick={() => setAbaAuth('login')}
            style={{
              padding: '10px 20px',
              borderRadius: '8px',
              border: 'none',
              fontWeight: 'bold',
              cursor: 'pointer',
              backgroundColor: abaAuth === 'login' ? '#1d4ed8' : 'white',
              color: abaAuth === 'login' ? 'white' : '#374151',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
          >
            Entrar
          </button>
          <button
            onClick={() => setAbaAuth('cadastro')}
            style={{
              padding: '10px 20px',
              borderRadius: '8px',
              border: 'none',
              fontWeight: 'bold',
              cursor: 'pointer',
              backgroundColor: abaAuth === 'cadastro' ? '#1d4ed8' : 'white',
              color: abaAuth === 'cadastro' ? 'white' : '#374151',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
          >
            Criar Conta
          </button>
        </nav>

        {abaAuth === 'login' ? <LoginUsuario /> : <CadastroUsuario />}
      </div>
    );
  }

  // 2. SE O USUÁRIO ESTIVER LOGADO: Exibe o sistema principal
  const botoesAba = [
    { id: 'lista', titulo: 'Catálogo' },
    { id: 'cadastro', titulo: 'Cadastrar Item' }
  ];

  return (
    <div style={{
      backgroundColor: '#f3f4f6',
      minHeight: '100vh',
      padding: '40px 20px',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', maxWidth: '800px', margin: '0 auto 30px auto' }}>
        <nav style={{ display: 'flex', gap: '10px', fontFamily: 'sans-serif' }}>
          {botoesAba.map((aba) => (
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

        {/* Botão de Sair */}
        <button
          onClick={handleLogout}
          style={{
            padding: '10px 16px',
            borderRadius: '8px',
            border: '1px solid #dc2626',
            backgroundColor: 'white',
            color: '#dc2626',
            fontWeight: 'bold',
            cursor: 'pointer'
          }}
        >
          Sair
        </button>
      </div>

      {/* Renderização dinâmica do componente com as props atualizadas */}
      {renderizarTelaAtiva()}
    </div>
  );
}

export default App;