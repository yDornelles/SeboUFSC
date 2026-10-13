import { useState, useEffect } from 'react';

export default function ModalItem({ item, onClose, usuarioLogado, onVerPerfil }) {
  const [mensagem, setMensagem] = useState({ texto: '', erro: false });
  const [carregando, setCarregando] = useState(false);
  const [jaTemInteresse, setJaTemInteresse] = useState(false);

  if (!item) return null;

  const idUsuarioAtual = usuarioLogado?.uid || usuarioLogado?.id;
  const ehDonoDoItem = idUsuarioAtual === item.usuarioId;

  // Verifica se o item já está na lista de interesses do usuário ao abrir o modal
  useEffect(() => {
    const verificarInteresse = async () => {
      if (!idUsuarioAtual) return;
      try {
        const resposta = await fetch(`http://localhost:8080/api/interesses/listar/${idUsuarioAtual}`);
        if (resposta.ok) {
          const interesses = await resposta.json();
          const encontrado = interesses.some((i) => i.id === item.id);
          setJaTemInteresse(encontrado);
        }
      } catch (err) {
        console.error('Erro ao verificar interesses', err);
      }
    };

    verificarInteresse();
  }, [idUsuarioAtual, item.id]);

  const formatarPreco = (valor, opcao) => {
    if (opcao === 'SO_TROCA') return 'Troca';
    if (!valor) return 'R$ 0,00';
    return `R$ ${Number(valor).toFixed(2).replace('.', ',')}`;
  };

  const handleToggleInteresse = async () => {
    if (!idUsuarioAtual) {
      setMensagem({ texto: 'Você precisa estar logado para gerenciar interesses.', erro: true });
      return;
    }

    setCarregando(true);
    setMensagem({ texto: '', erro: false });

    try {
      if (jaTemInteresse) {
        // --- REMOVER INTERESSE ---
        const response = await fetch(`http://localhost:8080/api/interesses/remover/${idUsuarioAtual}/${item.id}`, {
          method: 'DELETE'
        });
        const dados = await response.json();
        if (!response.ok) throw new Error(dados.mensagem || 'Erro ao remover dos interesses.');
        
        setJaTemInteresse(false);
        setMensagem({ texto: '🗑️ Item removido dos interesses.', erro: false });
      } else {
        // --- ADICIONAR INTERESSE ---
        const response = await fetch('http://localhost:8080/api/interesses/adicionar', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ usuarioId: idUsuarioAtual, itemId: item.id })
        });
        const dados = await response.json();
        if (!response.ok) throw new Error(dados.mensagem || 'Erro ao adicionar aos interesses.');
        
        setJaTemInteresse(true);
        setMensagem({ texto: '✅ ' + dados.mensagem, erro: false });
      }
    } catch (erro) {
      setMensagem({ texto: '❌ ' + erro.message, erro: true });
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div style={modalOverlayStyle} onClick={onClose}>
      <div style={modalContentStyle} onClick={(e) => e.stopPropagation()}>
        <button style={closeButtonStyle} onClick={onClose}>✖</button>
        
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
          <div style={{ flex: '1 1 250px', display: 'flex', justifyContent: 'center', alignItems: 'flex-start' }}>
            <img 
              src={item.imagem || 'https://via.placeholder.com/250x350?text=Sem+Imagem'} 
              alt={item.titulo} 
              style={{ width: '100%', maxWidth: '250px', borderRadius: '8px', objectFit: 'cover', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}
            />
          </div>

          <div style={{ flex: '2 1 300px' }}>
            <h2 style={{ margin: '0 0 5px 0', color: '#1f2937' }}>{item.titulo}</h2>
            
            <p style={{ margin: '0 0 10px 0', color: '#4b5563', fontSize: '16px' }}>
              {item.autor} {item.editora && `- ${item.editora}`}
            </p>

            {/* Nome do proprietário clicável que abre o perfil */}
            <div style={{ marginBottom: '20px' }}>
              <span style={{ fontSize: '14px', color: '#6b7280' }}>Anunciado por: </span>
              <button 
                onClick={() => {
                  onClose();
                  if (onVerPerfil) {
                    onVerPerfil(item.usuarioId);
                  }
                }}
                style={{
                  background: 'none', border: 'none', color: '#1d4ed8',
                  fontWeight: 'bold', fontSize: '15px', cursor: 'pointer',
                  padding: 0, textDecoration: 'underline'
                }}
              >
                @{item.nomeProprietario || 'Usuário'}
              </button>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '20px', backgroundColor: '#f9fafb', padding: '15px', borderRadius: '8px' }}>
              <div>
                <span style={labelStyle}>Mídia</span>
                <span style={valorStyle}>{item.tipoMidia}</span>
              </div>
              <div>
                <span style={labelStyle}>Conservação</span>
                <span style={valorStyle}>{item.estadoConservacao.replace('_', ' ')}</span>
              </div>
              <div>
                <span style={labelStyle}>Negociação</span>
                <span style={valorStyle}>{item.opcaoNegocio.replace('_', ' ')}</span>
              </div>
              <div>
                <span style={labelStyle}>Preço</span>
                <span style={{...valorStyle, color: '#16a34a', fontWeight: 'bold'}}>
                  {formatarPreco(item.preco, item.opcaoNegocio)}
                </span>
              </div>
            </div>

            <div style={{ marginBottom: '20px' }}>
              <span style={labelStyle}>Descrição</span>
              <p style={{ margin: '5px 0 0 0', color: '#4b5563', lineHeight: '1.5' }}>
                {item.descricao || 'Nenhuma descrição informada pelo proprietário.'}
              </p>
            </div>

            {mensagem.texto && (
              <div style={{ 
                padding: '10px', marginBottom: '15px', borderRadius: '6px', fontWeight: 'bold', textAlign: 'center',
                backgroundColor: mensagem.erro ? '#fee2e2' : '#dcfce7',
                color: mensagem.erro ? '#991b1b' : '#166534'
              }}>
                {mensagem.texto}
              </div>
            )}

            {!ehDonoDoItem ? (
              <button 
                onClick={handleToggleInteresse} 
                disabled={carregando}
                style={{
                  width: '100%', padding: '12px', color: 'white', border: 'none', borderRadius: '6px', fontWeight: 'bold', fontSize: '16px', cursor: carregando ? 'not-allowed' : 'pointer',
                  backgroundColor: jaTemInteresse ? '#dc2626' : '#1d4ed8', 
                  transition: 'background-color 0.2s'
                }}
              >
                {carregando ? 'Processando...' : (jaTemInteresse ? 'Remover Interesse' : 'Manifestar Interesse')}
              </button>
            ) : (
              <div style={{ padding: '12px', textAlign: 'center', backgroundColor: '#f3f4f6', color: '#6b7280', borderRadius: '6px', fontWeight: 'bold' }}>
                Este item pertence a você.
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

const modalOverlayStyle = { position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0, 0, 0, 0.6)', display: 'flex', justifyContent: 'center', alignItems: 'center', padding: '20px', zIndex: 1000 };
const modalContentStyle = { backgroundColor: 'white', padding: '30px', borderRadius: '12px', maxWidth: '750px', width: '100%', maxHeight: '90vh', overflowY: 'auto', position: 'relative', boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)' };
const closeButtonStyle = { position: 'absolute', top: '15px', right: '15px', background: 'none', border: 'none', fontSize: '20px', color: '#6b7280', cursor: 'pointer' };
const labelStyle = { display: 'block', fontSize: '12px', color: '#6b7280', textTransform: 'uppercase', fontWeight: 'bold' };
const valorStyle = { display: 'block', fontSize: '16px', color: '#1f2937', marginTop: '2px' };