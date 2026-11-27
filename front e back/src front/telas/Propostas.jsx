import { useCallback, useEffect, useState } from 'react';
import CartaoProposta from '../componentes/CartaoProposta';

const API = 'http://localhost:8080/api';

const FILTROS = [
  { id: 'TODAS', titulo: 'Todas' },
  { id: 'AGUARDANDO_CONFIRMACAO', titulo: 'Aguardando' },
  { id: 'CONFIRMADA', titulo: 'Confirmadas' },
  { id: 'CANCELADA', titulo: 'Canceladas' },
];

export default function Propostas({ usuario, propostaIdAlvo, onAtualizar }) {
  const meuId = usuario?.uid || usuario?.id;
  const [propostas, setPropostas] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [feedback, setFeedback] = useState({ texto: '', erro: false });
  const [enviandoId, setEnviandoId] = useState(null);
  const [filtro, setFiltro] = useState('TODAS');

  const carregar = useCallback(async () => {
    try {
      const resposta = await fetch(`${API}/propostas/usuario/${meuId}`);
      if (!resposta.ok) throw new Error('Erro ao buscar propostas.');
      setPropostas(await resposta.json());
      setErro('');
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  }, [meuId]);

  useEffect(() => {
    if (meuId) carregar();
  }, [meuId, carregar]);

  // Ao chegar por uma notificação, rola até a proposta indicada
  useEffect(() => {
    if (!propostaIdAlvo || carregando) return;
    document.getElementById(`proposta-${propostaIdAlvo}`)?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, [propostaIdAlvo, carregando]);

  const responder = async (proposta, aceite) => {
    if (!aceite && !window.confirm('Recusar cancela a proposta para todos os participantes. Deseja continuar?')) {
      return;
    }

    setEnviandoId(proposta.id);
    setFeedback({ texto: '', erro: false });

    try {
      const resposta = await fetch(`${API}/propostas/${proposta.id}/responder`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ usuarioId: meuId, aceite }),
      });
      const dados = await resposta.json();
      if (!resposta.ok) throw new Error(dados.mensagem || 'Erro ao responder a proposta.');

      const textos = {
        CONFIRMADA: '🎉 Todos aceitaram! A troca foi confirmada.',
        CANCELADA: 'Proposta recusada e cancelada.',
        AGUARDANDO_CONFIRMACAO: '✅ Seu aceite foi registrado. Aguardando os demais participantes.',
      };
      setFeedback({ texto: textos[dados.status], erro: false });
    } catch (err) {
      setFeedback({ texto: '❌ ' + err.message, erro: true });
    } finally {
      setEnviandoId(null);
      await carregar();
      onAtualizar?.();
    }
  };

  const visiveis = propostas.filter((p) => filtro === 'TODAS' || p.status === filtro);

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <h2 style={{ margin: '0 0 16px 0', color: '#1f2937' }}>Propostas de troca</h2>

      <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginBottom: '16px' }}>
        {FILTROS.map((f) => (
          <button
            key={f.id}
            onClick={() => setFiltro(f.id)}
            style={{
              padding: '6px 14px', borderRadius: '999px', border: 'none', cursor: 'pointer', fontWeight: 'bold', fontSize: '13px',
              backgroundColor: filtro === f.id ? '#1d4ed8' : 'white',
              color: filtro === f.id ? 'white' : '#374151',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            }}
          >
            {f.titulo}
          </button>
        ))}
      </div>

      {feedback.texto && (
        <div style={{
          padding: '10px', marginBottom: '16px', borderRadius: '6px', fontWeight: 'bold', textAlign: 'center',
          backgroundColor: feedback.erro ? '#fee2e2' : '#dcfce7',
          color: feedback.erro ? '#991b1b' : '#166534',
        }}>
          {feedback.texto}
        </div>
      )}

      {carregando && <div style={estadoStyle}>Carregando propostas...</div>}

      {!carregando && erro && (
        <div style={{ ...estadoStyle, backgroundColor: '#fee2e2', color: '#991b1b' }}>❌ {erro}</div>
      )}

      {!carregando && !erro && visiveis.length === 0 && (
        <div style={estadoStyle}>
          {propostas.length === 0
            ? 'Você ainda não tem propostas. Quando o sistema encontrar uma troca possível entre os interesses da comunidade, ela aparece aqui.'
            : 'Nenhuma proposta neste filtro.'}
        </div>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {visiveis.map((p) => (
          <CartaoProposta
            key={p.id}
            proposta={p}
            meuId={meuId}
            enviando={enviandoId === p.id}
            destacada={p.id === propostaIdAlvo}
            onResponder={(aceite) => responder(p, aceite)}
          />
        ))}
      </div>
    </div>
  );
}

const estadoStyle = {
  padding: '40px 20px',
  textAlign: 'center',
  color: '#6b7280',
  backgroundColor: 'white',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
};
