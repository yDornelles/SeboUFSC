import { useEffect, useState } from 'react';
import CartaoItem from '../componentes/CartaoItem';

function ListaItens() {
  const [itens, setItens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  useEffect(() => {
    buscarItens();
  }, []);

  const buscarItens = async () => {
    setCarregando(true);
    setErro('');

    try {
      const resposta = await fetch('http://localhost:8080/api/itens/listar');

      if (!resposta.ok) {
        throw new Error('Erro ao buscar itens. Verifique se o servidor Java está rodando.');
      }

      const dados = await resposta.json();
      setItens(dados);
    } catch (erro) {
      setErro(erro.message);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '0 10px', fontFamily: 'sans-serif' }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', flexWrap: 'wrap', gap: '10px', marginBottom: '20px' }}>
        <h2 style={{ margin: 0, color: '#1f2937' }}>Acervo da comunidade</h2>
        <span style={{ fontSize: '14px', color: '#6b7280' }}>
          {!carregando && !erro && `${itens.length} ${itens.length === 1 ? 'item disponível' : 'itens disponíveis'}`}
        </span>
      </div>

      {carregando && (
        <div style={estadoVazioStyle}>Carregando itens...</div>
      )}

      {!carregando && erro && (
        <div style={{ ...estadoVazioStyle, backgroundColor: '#fee2e2', color: '#991b1b' }}>
          ❌ {erro}
          <div style={{ marginTop: '10px' }}>
            <button onClick={buscarItens} style={tentarNovamenteStyle}>Tentar novamente</button>
          </div>
        </div>
      )}

      {!carregando && !erro && itens.length === 0 && (
        <div style={estadoVazioStyle}>
          Ainda não há itens disponíveis. Assim que alguém cadastrar uma mídia para troca ou venda, ela aparece aqui.
        </div>
      )}

      {!carregando && !erro && itens.length > 0 && (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
          gap: '20px',
        }}>
          {itens.map((item) => (
            <CartaoItem key={item.id} item={item} />
          ))}
        </div>
      )}
    </div>
  );
}

const estadoVazioStyle = {
  padding: '40px 20px',
  textAlign: 'center',
  color: '#6b7280',
  backgroundColor: 'white',
  borderRadius: '12px',
  boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
};

const tentarNovamenteStyle = {
  backgroundColor: '#991b1b',
  color: 'white',
  border: 'none',
  borderRadius: '6px',
  padding: '8px 16px',
  cursor: 'pointer',
  fontWeight: 'bold',
};

export default ListaItens;
