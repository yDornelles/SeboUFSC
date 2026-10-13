import { useState, useEffect } from 'react';
import { onAuthStateChanged, signOut } from 'firebase/auth';
import { auth } from './firebase';

import CadastroItem from './telas/CadastroItem';
import ListaItens from './telas/ListaItens';
import LoginUsuario from './telas/LoginUsuario';
import CadastroUsuario from './telas/CadastroUsuario';
import PerfilUsuario from './telas/PerfilUsuario';
import EditarItem from './telas/EditarItem'; 
import Propostas from './telas/Propostas';
import Notificacoes from './telas/Notificacoes';
import useContadores from './hooks/useContadores';

function App() {
  const [usuario, setUsuario] = useState(null);
  const [carregando, setCarregando] = useState(true);
  const [abaAtiva, setAbaAtiva] = useState('lista');
  const [abaAuth, setAbaAuth] = useState('login'); // 'login' ou 'cadastro'
  const [itemParaEditar, setItemParaEditar] = useState(null); 
  const [perfilIdAlvo, setPerfilIdAlvo] = useState(null); 
  const [propostaIdAlvo, setPropostaIdAlvo] = useState(null);

  // Selos das abas: notificações não lidas e propostas aguardando resposta do usuário
  const { naoLidas, pendentes, recarregar: recarregarContadores } = useContadores(usuario?.uid);

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

  // Renderiza a tela de acordo com a aba ativa
  const renderizarTelaAtiva = () => {
    switch (abaAtiva) {
      case 'cadastro':
        return <CadastroItem usuario={usuario} />;
      case 'perfil':
        return (
          <PerfilUsuario 
            usuarioLogado={usuario} 
            perfilId={perfilIdAlvo} 
            onEditarItem={(id) => {
              setItemParaEditar(id);
              setAbaAtiva('editar');
            }} 
            onVerPerfil={(idDono) => {
              setPerfilIdAlvo(idDono);
              setAbaAtiva('perfil');
            }}
          />
        );
      case 'propostas':
        return (
          <Propostas
            usuario={usuario}
            propostaIdAlvo={propostaIdAlvo}
            onAtualizar={recarregarContadores}
          />
        );
      case 'notificacoes':
        return (
          <Notificacoes
            usuario={usuario}
            onAtualizar={recarregarContadores}
            onAbrirProposta={(id) => {
              setPropostaIdAlvo(id);
              setAbaAtiva('propostas');
            }}
          />
        );
      case 'editar':
        return (
          <EditarItem 
            usuario={usuario} 
            itemId={itemParaEditar} 
            onVoltar={() => {
              setPerfilIdAlvo(null);
              setAbaAtiva('perfil');
            }} 
          />
        );
      case 'lista':
      default:
        return (
          <ListaItens 
            usuario={usuario} 
            onVerPerfil={(idDono) => {
              setPerfilIdAlvo(idDono);
              setAbaAtiva('perfil');
            }}
          />
        );
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
    { id: 'cadastro', titulo: 'Cadastrar Item' },
    { id: 'propostas', titulo: 'Propostas', selo: pendentes },
    { id: 'notificacoes', titulo: 'Notificações', selo: naoLidas },
    { id: 'perfil', titulo: 'Meu Perfil' }
  ];

  return (
    <div style={{
      backgroundColor: '#f3f4f6',
      minHeight: '100vh',
      padding: '40px 20px',
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', maxWidth: '800px', margin: '0 auto 30px auto', gap: '10px', flexWrap: 'wrap' }}>
        <nav style={{ display: 'flex', gap: '10px', fontFamily: 'sans-serif', flexWrap: 'wrap' }}>
          {botoesAba.map((aba) => (
            <button
              key={aba.id}
              onClick={() => {
                if (aba.id === 'perfil') {
                  setPerfilIdAlvo(null);
                }
                if (aba.id === 'propostas') {
                  setPropostaIdAlvo(null);
                }
                setAbaAtiva(aba.id);
                setItemParaEditar(null);
              }}
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
              {aba.selo > 0 && (
                <span style={{
                  marginLeft: '8px',
                  backgroundColor: '#dc2626',
                  color: 'white',
                  borderRadius: '999px',
                  padding: '2px 8px',
                  fontSize: '12px',
                }}>
                  {aba.selo}
                </span>
              )}
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