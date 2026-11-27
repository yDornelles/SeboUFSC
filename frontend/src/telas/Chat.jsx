import { useCallback, useEffect, useRef, useState } from 'react';

const API = 'http://localhost:8080/api';
const INTERVALO_MS = 3000; // atualização simples por polling (sem WebSocket)

const formatarHora = (data) =>
  new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export default function Chat({ usuario, propostaId, onVoltar, onAtualizar }) {
  const meuId = usuario?.uid || usuario?.id;
  const [proposta, setProposta] = useState(null);
  const [mensagens, setMensagens] = useState([]);
  const [texto, setTexto] = useState('');
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);
  const fimRef = useRef(null);

  const carregar = useCallback(async () => {
    try {
      const resProp = await fetch(`${API}/propostas/${propostaId}`);
      const dadosProp = await resProp.json();
      if (!resProp.ok) throw new Error(dadosProp.mensagem || 'Erro ao buscar a negociação.');
      setProposta(dadosProp);

      if (dadosProp.chatId != null) {
        const resHist = await fetch(`${API}/chats/${dadosProp.chatId}/historico`);
        if (!resHist.ok) throw new Error('Erro ao carregar o histórico.');
        setMensagens(await resHist.json());
      }
      setErro('');
    } catch (err) {
      setErro(err.message);
    }
  }, [propostaId]);

  useEffect(() => {
    carregar();
    const intervalo = setInterval(carregar, INTERVALO_MS);
    return () => clearInterval(intervalo);
  }, [carregar]);

  useEffect(() => {
    fimRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [mensagens.length]);

  const enviar = async (e) => {
    e.preventDefault();
    if (!texto.trim() || !proposta?.chatAtivo) return;
    setEnviando(true);
    try {
      const resposta = await fetch(`${API}/chats/${proposta.chatId}/mensagens`, {
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
      const resposta = await fetch(`${API}/chats/${proposta.chatId}/finalizar`, {
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

  const eu = proposta?.participantes.find((p) => p.id === meuId);
  const possoFinalizar = proposta?.chatAtivo && eu && eu.entrega.length > 0;

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <button onClick={onVoltar} style={botaoVoltar}>← Voltar às propostas</button>

      <h2 style={{ margin: '0 0 16px 0', color: '#1f2937' }}>
        Chat da negociação #{propostaId}
      </h2>

      {erro && (
        <div style={{ padding: '10px', marginBottom: '12px', borderRadius: '6px', backgroundColor: '#fee2e2', color: '#991b1b', fontWeight: 'bold', textAlign: 'center' }}>
          ❌ {erro}
        </div>
      )}

      {proposta && (
        <div style={cartao}>
          {/* Itens da negociação: título e foto */}
          <div style={{ display: 'flex', gap: '12px', overflowX: 'auto', paddingBottom: '8px' }}>
            {proposta.itens.map((item) => (
              <div key={item.id} style={{ flex: '0 0 90px', fontSize: '12px', color: '#4b5563' }}>
                <div style={{ width: '90px', height: '115px', backgroundColor: '#f3f4f6', borderRadius: '8px', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  {item.imagem ? (
                    <img src={item.imagem} alt={item.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} onError={(e) => { e.target.style.display = 'none'; }} />
                  ) : (
                    <span style={{ color: '#9ca3af' }}>Sem imagem</span>
                  )}
                </div>
                <div style={{ fontWeight: 'bold', marginTop: '6px', color: '#1f2937' }}>{item.titulo}</div>
                <div>de @{item.nomeProprietario}</div>
                <div style={{ color: '#6b7280' }}>{item.status}</div>
              </div>
            ))}
          </div>

          <div style={{ marginTop: '8px', fontSize: '13px', color: '#6b7280' }}>
            Participantes: {proposta.participantes.map((p) => `@${p.nomeUsuario}`).join(', ')}
          </div>
        </div>
      )}

      {/* Histórico */}
      <div style={{ ...cartao, marginTop: '16px', height: '340px', overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {proposta && proposta.chatId == null && (
          <p style={{ color: '#6b7280', textAlign: 'center' }}>
            O chat só é liberado depois que todos aceitarem a proposta.
          </p>
        )}
        {proposta?.chatId != null && mensagens.length === 0 && (
          <p style={{ color: '#6b7280', textAlign: 'center' }}>Nenhuma mensagem ainda. Comece a combinar a entrega!</p>
        )}
        {mensagens.map((m) => {
          const minha = m.remetenteId === meuId;
          return (
            <div key={m.id} style={{ alignSelf: minha ? 'flex-end' : 'flex-start', maxWidth: '75%' }}>
              <div style={{
                backgroundColor: minha ? '#1d4ed8' : '#f3f4f6',
                color: minha ? 'white' : '#1f2937',
                borderRadius: '12px',
                padding: '8px 12px',
                fontSize: '14px',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
              }}>
                {!minha && <strong style={{ display: 'block', fontSize: '12px' }}>@{m.remetenteNome}</strong>}
                {m.texto}
              </div>
              <div style={{ fontSize: '11px', color: '#9ca3af', textAlign: minha ? 'right' : 'left', marginTop: '2px' }}>
                {formatarHora(m.dataHora)}
              </div>
            </div>
          );
        })}
        <div ref={fimRef} />
      </div>

      {/* Envio / estado do chat */}
      {proposta?.chatAtivo ? (
        <form onSubmit={enviar} style={{ display: 'flex', gap: '8px', marginTop: '12px' }}>
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
        proposta?.chatId != null && (
          <div style={{ marginTop: '12px', padding: '12px', borderRadius: '8px', backgroundColor: '#e5e7eb', color: '#374151', textAlign: 'center', fontWeight: 'bold' }}>
            Este chat foi encerrado.
          </div>
        )
      )}

      {possoFinalizar && (
        <button onClick={finalizar} style={{ ...botao, backgroundColor: '#166534', width: '100%', marginTop: '12px' }}>
          Finalizar transação
        </button>
      )}
    </div>
  );
}

const cartao = {
  backgroundColor: 'white',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
  padding: '16px',
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
