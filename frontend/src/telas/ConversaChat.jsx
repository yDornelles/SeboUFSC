import { useCallback, useEffect, useRef, useState } from 'react';

const API = 'http://localhost:8080/api';
const INTERVALO_MS = 3000; // atualização simples por polling (sem WebSocket)

const formatarHora = (data) =>
  new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export default function ConversaChat({ usuario, chatId, onVoltar, onAtualizar }) {
  const meuId = usuario?.uid || usuario?.id;
  const [chat, setChat] = useState(null);
  const [mensagens, setMensagens] = useState([]);
  const [texto, setTexto] = useState('');
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);
  const areaRef = useRef(null);

  const carregar = useCallback(async () => {
    try {
      const [resChat, resHist] = await Promise.all([
        fetch(`${API}/chats/${chatId}`),
        fetch(`${API}/chats/${chatId}/historico`),
      ]);
      const dadosChat = await resChat.json();
      if (!resChat.ok) throw new Error(dadosChat.mensagem || 'Erro ao buscar a conversa.');
      if (!resHist.ok) throw new Error('Erro ao carregar o histórico.');
      setChat(dadosChat);
      setMensagens(await resHist.json());
      setErro('');
    } catch (err) {
      setErro(err.message);
    }
  }, [chatId]);

  useEffect(() => {
    carregar();
    const intervalo = setInterval(carregar, INTERVALO_MS);
    return () => clearInterval(intervalo);
  }, [carregar]);

  // Rola só a área de mensagens (não a página inteira) quando chega mensagem nova
  useEffect(() => {
    const area = areaRef.current;
    if (area) area.scrollTop = area.scrollHeight;
  }, [mensagens.length]);

  const enviar = async (e) => {
    e.preventDefault();
    if (!texto.trim() || !chat?.ativo) return;
    setEnviando(true);
    try {
      const resposta = await fetch(`${API}/chats/${chatId}/mensagens`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usuarioId: meuId, msg: texto }),
      });
      if (!resposta.ok) {
        const dados = await resposta.json();
        throw new Error(dados.mensagem || 'Erro ao enviar a mensagem.');
      }
      setTexto('');
      await carregar();
    } catch (err) {
      setErro(err.message);
    } finally {
      setEnviando(false);
    }
  };

  const finalizar = async () => {
    if (!window.confirm('Finalizar a transação marca os itens como vendidos/trocados e encerra o chat. Deseja continuar?')) {
      return;
    }
    try {
      const resposta = await fetch(`${API}/chats/${chatId}/finalizar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usuarioId: meuId }),
      });
      if (!resposta.ok) {
        const dados = await resposta.json();
        throw new Error(dados.mensagem || 'Erro ao finalizar a transação.');
      }
      await carregar();
      onAtualizar?.();
    } catch (err) {
      setErro(err.message);
    }
  };

  const possoFinalizar = chat?.ativo && chat.itens.some((i) => i.usuarioId === meuId);
  const outros = chat?.participantes.filter((p) => p.id !== meuId) ?? [];

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <button onClick={onVoltar} style={botaoVoltar}>← Todas as conversas</button>

      {erro && (
        <div style={{ padding: '10px', marginBottom: '12px', borderRadius: '6px', backgroundColor: '#fee2e2', color: '#991b1b', fontWeight: 'bold', textAlign: 'center' }}>
          ❌ {erro}
        </div>
      )}

      {!chat && !erro && <div style={{ textAlign: 'center', color: '#6b7280' }}>Carregando conversa...</div>}

      {chat && (
        <div style={moldura}>
          {/* Cabeçalho: quem, itens (título e foto) e finalizar */}
          <div style={{ padding: '14px 16px', borderBottom: '1px solid #e5e7eb' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
              <div>
                <strong style={{ color: '#1f2937', fontSize: '16px' }}>
                  {outros.map((p) => `@${p.nomeUsuario}`).join(', ') || 'Negociação'}
                </strong>
                <div style={{ fontSize: '12px', color: '#6b7280' }}>Proposta #{chat.propostaId}</div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{
                  fontSize: '11px', fontWeight: 'bold', padding: '3px 10px', borderRadius: '999px',
                  color: chat.ativo ? '#166534' : '#374151',
                  backgroundColor: chat.ativo ? '#dcfce7' : '#e5e7eb',
                }}>
                  {chat.ativo ? 'Ativo' : 'Encerrado'}
                </span>
                {possoFinalizar && (
                  <button onClick={finalizar} style={{ ...botao, backgroundColor: '#166534', padding: '8px 14px', fontSize: '13px' }}>
                    Finalizar transação
                  </button>
                )}
              </div>
            </div>

            <div style={{ display: 'flex', gap: '12px', overflowX: 'auto', marginTop: '12px' }}>
              {chat.itens.map((item) => (
                <div key={item.id} style={{ display: 'flex', gap: '8px', alignItems: 'center', flex: '0 0 auto', backgroundColor: '#f9fafb', borderRadius: '8px', padding: '6px 10px 6px 6px' }}>
                  <div style={{ width: '38px', height: '50px', borderRadius: '4px', overflow: 'hidden', backgroundColor: '#e5e7eb', flexShrink: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '9px', color: '#9ca3af' }}>
                    {item.imagem ? (
                      <img src={item.imagem} alt={item.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} onError={(e) => { e.target.style.display = 'none'; }} />
                    ) : 'Sem img'}
                  </div>
                  <div style={{ fontSize: '12px', color: '#4b5563', maxWidth: '150px' }}>
                    <div style={{ fontWeight: 'bold', color: '#1f2937', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{item.titulo}</div>
                    <div>de @{item.nomeProprietario}</div>
                    <div style={{ color: '#6b7280' }}>{item.status}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Histórico */}
          <div ref={areaRef} style={{ height: '380px', overflowY: 'auto', padding: '16px', backgroundColor: '#f9fafb', display: 'flex', flexDirection: 'column', gap: '8px' }}>
            {mensagens.length === 0 && (
              <p style={{ color: '#6b7280', textAlign: 'center', margin: 'auto' }}>
                Nenhuma mensagem ainda. Comece a combinar a entrega!
              </p>
            )}
            {mensagens.map((m) => {
              const minha = m.remetenteId === meuId;
              return (
                <div key={m.id} style={{ alignSelf: minha ? 'flex-end' : 'flex-start', maxWidth: '75%' }}>
                  <div style={{
                    backgroundColor: minha ? '#1d4ed8' : 'white',
                    color: minha ? 'white' : '#1f2937',
                    boxShadow: minha ? 'none' : '0 1px 2px rgba(0,0,0,0.1)',
                    borderRadius: '12px',
                    padding: '8px 12px',
                    fontSize: '14px',
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-word',
                  }}>
                    {!minha && <strong style={{ display: 'block', fontSize: '12px', color: '#1d4ed8' }}>@{m.remetenteNome}</strong>}
                    {m.texto}
                  </div>
                  <div style={{ fontSize: '11px', color: '#9ca3af', textAlign: minha ? 'right' : 'left', marginTop: '2px' }}>
                    {formatarHora(m.dataHora)}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Envio */}
          {chat.ativo ? (
            <form onSubmit={enviar} style={{ display: 'flex', gap: '8px', padding: '12px 16px', borderTop: '1px solid #e5e7eb' }}>
              <input
                value={texto}
                onChange={(e) => setTexto(e.target.value)}
                placeholder="Digite sua mensagem..."
                maxLength={1000}
                style={{ flex: 1, padding: '12px', borderRadius: '8px', border: '1px solid #d1d5db', fontSize: '15px' }}
              />
              <button type="submit" disabled={enviando || !texto.trim()} style={{ ...botao, backgroundColor: '#1d4ed8' }}>
                Enviar
              </button>
            </form>
          ) : (
            <div style={{ padding: '14px', borderTop: '1px solid #e5e7eb', backgroundColor: '#e5e7eb', color: '#374151', textAlign: 'center', fontWeight: 'bold' }}>
              Este chat foi encerrado.
            </div>
          )}
        </div>
      )}
    </div>
  );
}

const moldura = {
  backgroundColor: 'white',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
  overflow: 'hidden',
};

const botao = {
  color: 'white',
  border: 'none',
  borderRadius: '8px',
  padding: '12px 20px',
  fontWeight: 'bold',
  fontSize: '15px',
  cursor: 'pointer',
};

const botaoVoltar = {
  backgroundColor: 'white',
  color: '#1d4ed8',
  border: '1px solid #1d4ed8',
  borderRadius: '6px',
  padding: '8px 14px',
  fontWeight: 'bold',
  cursor: 'pointer',
  marginBottom: '16px',
};
