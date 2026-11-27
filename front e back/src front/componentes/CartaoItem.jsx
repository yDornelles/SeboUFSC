const TIPO_MIDIA_LABELS = {
  LIVRO: 'Livro',
  REVISTA: 'Revista',
  HQ: 'HQ',
  MANGA: 'Mangá',
  CD: 'CD',
  DVD: 'DVD',
  VINIL: 'Vinil',
};

const ESTADO_CONSERVACAO_LABELS = {
  NOVO: 'Novo',
  SEMI_NOVO: 'Seminovo',
  USADO: 'Usado',
  DANIFICADO: 'Danificado',
};

const OPCAO_NEGOCIO_LABELS = {
  SO_VENDA: 'Venda',
  SO_TROCA: 'Troca',
  TROCA_E_VENDA: 'Venda ou troca',
};

const formatarPreco = (preco) =>
  preco.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

function CartaoItem({ item }) {
  const ehSoTroca = item.opcaoNegocio === 'SO_TROCA';

  return (
    <article
      style={{
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: 'white',
        borderRadius: '12px',
        boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
        overflow: 'hidden',
        fontFamily: 'sans-serif',
      }}
    >
      <div
        style={{
          width: '100%',
          height: '180px',
          backgroundColor: '#f3f4f6',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          overflow: 'hidden',
        }}
      >
        {item.imagem ? (
          <img
            src={item.imagem}
            alt={`Capa de ${item.titulo}`}
            style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            onError={(e) => { e.target.style.display = 'none'; }}
          />
        ) : (
          <span style={{ color: '#9ca3af', fontSize: '14px' }}>Sem imagem</span>
        )}
      </div>

      <div style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px', flex: 1 }}>
        <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
          <span style={badgeStyle}>{TIPO_MIDIA_LABELS[item.tipoMidia] ?? item.tipoMidia}</span>
          <span style={badgeStyle}>{ESTADO_CONSERVACAO_LABELS[item.estadoConservacao] ?? item.estadoConservacao}</span>
        </div>

        <h3 style={{ margin: 0, fontSize: '17px', color: '#1f2937' }}>{item.titulo}</h3>
        <p style={{ margin: 0, fontSize: '14px', color: '#6b7280' }}>
          {item.autor}{item.editora ? ` · ${item.editora}` : ''}
        </p>

        {item.descricao && (
          <p style={{
            margin: 0, fontSize: '13px', color: '#4b5563',
            display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden',
          }}>
            {item.descricao}
          </p>
        )}

        <div style={{
          marginTop: 'auto', paddingTop: '8px', display: 'flex',
          alignItems: 'center', justifyContent: 'space-between',
        }}>
          <span style={{
            fontWeight: 'bold', fontSize: '16px',
            color: ehSoTroca ? '#1d4ed8' : '#166534',
          }}>
            {ehSoTroca ? 'Troca' : formatarPreco(item.preco)}
          </span>
          <span style={{ fontSize: '12px', color: '#6b7280' }}>
            {OPCAO_NEGOCIO_LABELS[item.opcaoNegocio] ?? item.opcaoNegocio}
          </span>
        </div>
      </div>
    </article>
  );
}

const badgeStyle = {
  fontSize: '11px',
  fontWeight: 'bold',
  color: '#374151',
  backgroundColor: '#e5e7eb',
  borderRadius: '999px',
  padding: '3px 10px',
};

export default CartaoItem;
