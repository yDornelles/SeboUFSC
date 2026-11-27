import { useCallback, useEffect, useState } from 'react';
import CartaoChat from '../componentes/CartaoChat';

const API = 'http://localhost:8080/api';
const INTERVALO_MS = 5000;

const FILTROS = [
  { id: 'TODOS', titulo: 'Todos' },
  { id: 'ATIVOS', titulo: 'Ativos' },
  { id: 'ENCERRADOS', titulo: 'Encerrados' },
];

// Aba "Chat": lista todas as conversas de que o usuário participa
export default function Chat({ usuario, onAbrirChat }) {
  const meuId = usuario?.uid || usuario?.id;
  const [chats, setChats] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [filtro, setFiltro] = useState('TODOS');

  const carregar = useCallback(async () => {
    try {
      const resposta = await fetch(`${API}/chats/usuario/${meuId}`);
      if (!resposta.ok) throw new Error('Erro ao buscar conversas.');
      setChats(await resposta.json());
      setErro('');
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  }, [meuId]);

  useEffect(() => {
    if (!meuId) return;
    carregar();
    const intervalo = setInterval(carregar, INTERVALO_MS);
    return () => clearInterval(intervalo);
  }, [meuId, carregar]);

  const visiveis = chats.filter((c) =>
    filtro === 'TODOS' || (filtro === 'ATIVOS' ? c.ativo : !c.ativo)
  );

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <h2 style={{ margin: '0 0 16px 0', color: '#1f2937' }}>Conversas</h2>

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

      {carregando && <div style={estadoStyle}>Carregando conversas...</div>}

      {!carregando && erro && (
        <div style={{ ...estadoStyle, backgroundColor: '#fee2e2', color: '#991b1b' }}>❌ {erro}</div>
      )}

      {!carregando && !erro && visiveis.length === 0 && (
        <div style={estadoStyle}>
          {chats.length === 0
            ? 'Você ainda não tem conversas. O chat é liberado quando todos os participantes aceitam uma proposta.'
            : 'Nenhuma conversa neste filtro.'}
        </div>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
        {visiveis.map((c) => (
          <CartaoChat key={c.id} chat={c} meuId={meuId} onAbrir={() => onAbrirChat(c.id)} />
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
