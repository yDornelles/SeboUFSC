import { useCallback, useEffect, useState } from 'react';

const API = 'http://localhost:8080/api';

const formatarData = (data) =>
  new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export default function Notificacoes({ usuario, onAtualizar, onAbrirProposta, onVerPerfil }) {
  const meuId = usuario?.uid || usuario?.id;
  const [notificacoes, setNotificacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregar = useCallback(async () => {
    try {
      const resposta = await fetch(`${API}/notificacoes/${meuId}`);
      if (!resposta.ok) throw new Error('Erro ao buscar notificações.');
      setNotificacoes(await resposta.json());
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

  const marcarComoLida = async (notificacao) => {
    if (notificacao.lida) return;
    setNotificacoes((lista) =>
      lista.map((n) => (n.idNotificacao === notificacao.idNotificacao ? { ...n, lida: true } : n))
    );
    try {
      await fetch(`${API}/notificacoes/${notificacao.idNotificacao}/lida`, { method: 'PUT' });
    } catch (err) {
      console.error('Erro ao marcar como lida', err);
    }
    onAtualizar?.();
  };

  const marcarTodasComoLidas = async () => {
    setNotificacoes((lista) => lista.map((n) => ({ ...n, lida: true })));
    try {
      await fetch(`${API}/notificacoes/usuario/${meuId}/lidas`, { method: 'PUT' });
    } catch (err) {
      console.error('Erro ao marcar todas como lidas', err);
    }
    onAtualizar?.();
  };

  const abrirProposta = async (notificacao) => {
    await marcarComoLida(notificacao);
    onAbrirProposta(notificacao.propostaId);
  };

  const verPerfilUsuario = async (notificacao, idUsuarioAlvo) => {
    await marcarComoLida(notificacao);
    if (onVerPerfil && idUsuarioAlvo) {
      onVerPerfil(idUsuarioAlvo);
    }
  };

  const naoLidas = notificacoes.filter((n) => !n.lida).length;

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px', marginBottom: '16px' }}>
        <h2 style={{ margin: 0, color: '#1f2937' }}>Notificações</h2>
        {naoLidas > 0 && (
          <button
            onClick={marcarTodasComoLidas}
            style={{ backgroundColor: 'white', color: '#1d4ed8', border: '1px solid #1d4ed8', borderRadius: '6px', padding: '8px 14px', fontWeight: 'bold', cursor: 'pointer' }}
          >
            Marcar todas como lidas
          </button>
        )}
      </div>

      {carregando && <div style={estadoStyle}>Carregando notificações...</div>}

      {!carregando && erro && (
        <div style={{ ...estadoStyle, backgroundColor: '#fee2e2', color: '#991b1b' }}>❌ {erro}</div>
      )}

      {!carregando && !erro && notificacoes.length === 0 && (
        <div style={estadoStyle}>Você não tem notificações.</div>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {notificacoes.map((n) => {
          // Substitua 'remetenteId' pelo nome exato do campo que envia o ID do usuário no seu DTO de notificação do Java
          const idRemetente = n.remetenteId || n.usuarioOrigemId; 

          return (
            <div
              key={n.idNotificacao}
              onClick={() => marcarComoLida(n)}
              style={{
                backgroundColor: n.lida ? 'white' : '#eff6ff',
                borderLeft: `4px solid ${n.lida ? '#e5e7eb' : '#1d4ed8'}`,
                borderRadius: '8px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.08)',
                padding: '14px 16px',
                cursor: n.lida ? 'default' : 'pointer',
              }}
            >
              <p style={{ margin: 0, color: '#1f2937', fontWeight: n.lida ? 'normal' : 'bold' }}>{n.mensagem}</p>
              
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '12px', gap: '10px', flexWrap: 'wrap' }}>
                <span style={{ fontSize: '12px', color: '#6b7280' }}>{formatarData(n.dataEnvio)}</span>
                
                <div style={{ display: 'flex', gap: '8px' }}>
                  {idRemetente && (
                    <button
                      onClick={(e) => { e.stopPropagation(); verPerfilUsuario(n, idRemetente); }}
                      style={{ backgroundColor: '#4b5563', color: 'white', border: 'none', borderRadius: '6px', padding: '6px 12px', fontWeight: 'bold', fontSize: '13px', cursor: 'pointer' }}
                    >
                      Ver perfil
                    </button>
                  )}

                  {n.propostaId != null && (
                    <button
                      onClick={(e) => { e.stopPropagation(); abrirProposta(n); }}
                      style={{ backgroundColor: '#1d4ed8', color: 'white', border: 'none', borderRadius: '6px', padding: '6px 12px', fontWeight: 'bold', fontSize: '13px', cursor: 'pointer' }}
                    >
                      Ver proposta
                    </button>
                  )}
                </div>
              </div>
            </div>
          );
        })}
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