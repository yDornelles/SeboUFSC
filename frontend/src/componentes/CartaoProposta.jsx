const STATUS_INFO = {
  AGUARDANDO_CONFIRMACAO: { texto: 'Aguardando confirmação', cor: '#92400e', fundo: '#fef3c7' },
  CONFIRMADA: { texto: 'Confirmada', cor: '#166534', fundo: '#dcfce7' },
  CANCELADA: { texto: 'Cancelada', cor: '#991b1b', fundo: '#fee2e2' },
};

const formatarData = (data) =>
  new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

function CartaoProposta({ proposta, meuId, enviando, destacada, onResponder }) {
  const status = STATUS_INFO[proposta.status] ?? STATUS_INFO.AGUARDANDO_CONFIRMACAO;
  const itensPorId = Object.fromEntries(proposta.itens.map((i) => [i.id, i]));
  const eu = proposta.participantes.find((p) => p.id === meuId);
  const aguardando = proposta.status === 'AGUARDANDO_CONFIRMACAO';
  const aceitos = proposta.participantes.filter((p) => p.aceitou).length;

  const titulos = (ids) =>
    ids.map((id) => `“${itensPorId[id]?.titulo ?? 'item'}”`).join(', ') || '—';

  return (
    <article
      id={`proposta-${proposta.id}`}
      style={{
        backgroundColor: 'white',
        borderRadius: '12px',
        boxShadow: destacada ? '0 0 0 3px #1d4ed8, 0 4px 6px rgba(0,0,0,0.1)' : '0 4px 6px rgba(0,0,0,0.1)',
        padding: '20px',
        fontFamily: 'sans-serif',
      }}
    >
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '8px' }}>
        <h3 style={{ margin: 0, color: '#1f2937' }}>Proposta de troca #{proposta.id}</h3>
        <span style={{ fontSize: '12px', fontWeight: 'bold', padding: '4px 12px', borderRadius: '999px', color: status.cor, backgroundColor: status.fundo }}>
          {status.texto}
        </span>
      </div>
      <p style={{ margin: '4px 0 16px 0', fontSize: '13px', color: '#6b7280' }}>
        Criada em {formatarData(proposta.data)}
        {aguardando && ` · ${aceitos}/${proposta.participantes.length} aceites`}
      </p>

      {/* Itens envolvidos */}
      <div style={{ display: 'flex', gap: '10px', overflowX: 'auto', paddingBottom: '8px', marginBottom: '12px' }}>
        {proposta.itens.map((item) => (
          <div key={item.id} style={{ flex: '0 0 110px', fontSize: '12px', color: '#4b5563' }}>
            <div style={{ width: '110px', height: '140px', backgroundColor: '#f3f4f6', borderRadius: '8px', overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              {item.imagem ? (
                <img src={item.imagem} alt={item.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} onError={(e) => { e.target.style.display = 'none'; }} />
              ) : (
                <span style={{ color: '#9ca3af' }}>Sem imagem</span>
              )}
            </div>
            <div style={{ fontWeight: 'bold', marginTop: '6px', color: '#1f2937' }}>{item.titulo}</div>
            <div>de @{item.nomeProprietario}</div>
          </div>
        ))}
      </div>

      {/* Quem entrega / recebe o quê */}
      <ul style={{ listStyle: 'none', margin: '0 0 16px 0', padding: 0, display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {proposta.participantes.map((p) => (
          <li key={p.id} style={{ fontSize: '14px', color: '#374151', backgroundColor: '#f9fafb', borderRadius: '8px', padding: '10px 12px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: '8px' }}>
              <strong>@{p.nomeUsuario}{p.id === meuId ? ' (você)' : ''}</strong>
              <span style={{ fontSize: '13px', color: p.aceitou ? '#166534' : '#6b7280' }}>
                {p.aceitou ? '✔ Aceitou' : aguardando ? '⏳ Aguardando' : '—'}
              </span>
            </div>
            <div style={{ marginTop: '4px', fontSize: '13px' }}>
              Entrega {titulos(p.entrega)} e recebe {titulos(p.recebe)}
            </div>
          </li>
        ))}
      </ul>

      {/* Ações */}
      {aguardando && eu && !eu.aceitou && (
        <div style={{ display: 'flex', gap: '10px' }}>
          <button
            disabled={enviando}
            onClick={() => onResponder(true)}
            style={{ ...botaoAcao, backgroundColor: '#166534' }}
          >
            {enviando ? 'Enviando...' : 'Aceitar troca'}
          </button>
          <button
            disabled={enviando}
            onClick={() => onResponder(false)}
            style={{ ...botaoAcao, backgroundColor: '#dc2626' }}
          >
            Recusar
          </button>
        </div>
      )}
      {aguardando && eu?.aceitou && (
        <p style={{ margin: 0, fontSize: '14px', color: '#6b7280' }}>
          Você já aceitou. Aguardando os demais participantes.
        </p>
      )}
    </article>
  );
}

const botaoAcao = {
  flex: 1,
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  padding: '12px',
  fontWeight: 'bold',
  fontSize: '15px',
  cursor: 'pointer',
};

export default CartaoProposta;
