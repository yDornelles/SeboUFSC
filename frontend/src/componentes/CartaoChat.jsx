const formatarData = (data) =>
  new Date(data).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

function CartaoChat({ chat, meuId, onAbrir }) {
  const outros = chat.participantes.filter((p) => p.id !== meuId);
  const ultima = chat.ultimaMensagem;
  const titulos = chat.itens.map((i) => `“${i.titulo}”`).join(' · ');

  return (
    <article
      onClick={onAbrir}
      style={{
        backgroundColor: 'white',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
        padding: '16px',
        display: 'flex',
        gap: '14px',
        cursor: 'pointer',
        fontFamily: 'sans-serif',
        opacity: chat.ativo ? 1 : 0.8,
      }}
    >
      {/* Miniaturas dos itens */}
      <div style={{ display: 'flex', flexShrink: 0 }}>
        {chat.itens.slice(0, 3).map((item, idx) => (
          <div
            key={item.id}
            style={{
              width: '48px', height: '62px', borderRadius: '6px', overflow: 'hidden',
              backgroundColor: '#f3f4f6', border: '2px solid white', boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
              marginLeft: idx === 0 ? 0 : '-18px', display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontSize: '10px', color: '#9ca3af',
            }}
          >
            {item.imagem ? (
              <img src={item.imagem} alt={item.titulo} style={{ width: '100%', height: '100%', objectFit: 'cover' }} onError={(e) => { e.target.style.display = 'none'; }} />
            ) : (
              'Sem img'
            )}
          </div>
        ))}
      </div>

      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '8px' }}>
          <strong style={{ color: '#1f2937', fontSize: '15px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
            {outros.length > 0 ? outros.map((p) => `@${p.nomeUsuario}`).join(', ') : 'Negociação'}
          </strong>
          <span style={{
            fontSize: '11px', fontWeight: 'bold', padding: '3px 10px', borderRadius: '999px', flexShrink: 0,
            color: chat.ativo ? '#166534' : '#374151',
            backgroundColor: chat.ativo ? '#dcfce7' : '#e5e7eb',
          }}>
            {chat.ativo ? 'Ativo' : 'Encerrado'}
          </span>
        </div>

        <div style={{ fontSize: '12px', color: '#6b7280', margin: '2px 0 6px 0', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
          Proposta #{chat.propostaId} · {titulos}
        </div>

        <div style={{ fontSize: '13px', color: ultima ? '#374151' : '#9ca3af', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
          {ultima
            ? `${ultima.remetenteId === meuId ? 'Você' : '@' + ultima.remetenteNome}: ${ultima.texto}`
            : 'Nenhuma mensagem ainda. Comece a combinar a entrega!'}
        </div>
        {ultima && (
          <div style={{ fontSize: '11px', color: '#9ca3af', marginTop: '2px' }}>{formatarData(ultima.dataHora)}</div>
        )}
      </div>
    </article>
  );
}

export default CartaoChat;
