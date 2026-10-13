import { useState, useEffect } from 'react';
import CartaoItem from '../componentes/CartaoItem';

export default function PerfilUsuario({ usuarioLogado, perfilId }) {
  const [usuario, setUsuario] = useState(null);
  const [interesses, setInteresses] = useState([]);
  const [editando, setEditando] = useState(false);
  
  const [formData, setFormData] = useState({
    nome: '',
    sobrenome: '',
    nomeUsuario: ''
  });

  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);

  const idAlvo = perfilId || usuarioLogado?.uid || usuarioLogado?.id;
  const ehProprioPerfil = usuarioLogado && (usuarioLogado.uid === idAlvo || usuarioLogado.id === idAlvo);

  useEffect(() => {
    if (idAlvo) {
      carregarDadosPerfil();
    }
  }, [idAlvo]);

  const carregarDadosPerfil = async () => {
    setCarregando(true);
    try {
      const resUsuario = await fetch(`http://localhost:8080/api/usuarios/${idAlvo}`);
      if (!resUsuario.ok) throw new Error('Erro ao carregar dados do usuário.');
      const dadosUsuario = await resUsuario.json();
      setUsuario(dadosUsuario);
      setFormData({
        nome: dadosUsuario.nome || '',
        sobrenome: dadosUsuario.sobrenome || '',
        nomeUsuario: dadosUsuario.nomeUsuario || ''
      });

      const resInteresses = await fetch(`http://localhost:8080/api/interesses/listar/${idAlvo}`);
      if (resInteresses.ok) {
        const dadosInteresses = await resInteresses.json();
        setInteresses(dadosInteresses);
      }
    } catch (err) {
      setErro(err.message);
    } finally {
      setCarregando(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSalvarEdicao = async (e) => {
    e.preventDefault();
    setMensagem('');
    setErro('');

    try {
      const response = await fetch('http://localhost:8080/api/usuarios/atualizar', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id: idAlvo,
          ...formData,
          email: usuario.email,
        })
      });

      if (!response.ok) {
        const errText = await response.text();
        throw new Error(errText || 'Erro ao atualizar perfil.');
      }

      const dados = await response.json();
      setMensagem(dados.mensagem || 'Perfil atualizado com sucesso!');
      setEditando(false);
      carregarDadosPerfil();
    } catch (err) {
      setErro(err.message);
    }
  };

  if (carregando) return <div style={{ textAlign: 'center', padding: '40px' }}>Carregando perfil...</div>;
  if (erro && !usuario) return <div style={{ textAlign: 'center', padding: '40px', color: '#991b1b' }}>❌ {erro}</div>;

  return (
    <div style={{ maxWidth: '900px', margin: '0 auto', padding: '20px', fontFamily: 'sans-serif' }}>
      
      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2 style={{ margin: 0, color: '#1f2937' }}>Perfil de @{usuario?.nomeUsuario}</h2>
          {ehProprioPerfil && !editando && (
            <button 
              onClick={() => setEditando(true)}
              style={{ backgroundColor: '#1d4ed8', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}
            >
              Editar Perfil
            </button>
          )}
        </div>

        {mensagem && <div style={{ padding: '10px', marginBottom: '15px', backgroundColor: '#dcfce7', color: '#166534', borderRadius: '6px', fontWeight: 'bold' }}>✅ {mensagem}</div>}
        {erro && <div style={{ padding: '10px', marginBottom: '15px', backgroundColor: '#fee2e2', color: '#991b1b', borderRadius: '6px', fontWeight: 'bold' }}>❌ {erro}</div>}

        {!editando ? (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px', color: '#4b5563' }}>
            <div><strong>Nome:</strong> {usuario?.nome} {usuario?.sobrenome}</div>
            <div><strong>E-mail:</strong> {usuario?.email}</div>
          </div>
        ) : (
          <form onSubmit={handleSalvarEdicao}>
            <div style={{ display: 'flex', gap: '15px', marginBottom: '15px' }}>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Nome</label>
                <input type="text" name="nome" value={formData.nome} onChange={handleChange} required style={inputStyle} />
              </div>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Sobrenome</label>
                <input type="text" name="sobrenome" value={formData.sobrenome} onChange={handleChange} required style={inputStyle} />
              </div>
            </div>
            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '5px' }}>Nome de Usuário (@)</label>
            <input type="text" name="nomeUsuario" value={formData.nomeUsuario} onChange={handleChange} required style={inputStyle} />
            
            <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
              <button type="submit" style={{ backgroundColor: '#166534', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>Salvar</button>
              <button type="button" onClick={() => setEditando(false)} style={{ backgroundColor: '#6b7280', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>Cancelar</button>
            </div>
          </form>
        )}
      </div>

      <div style={{ backgroundColor: 'white', padding: '30px', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
        <h3 style={{ marginTop: 0, color: '#1f2937', marginBottom: '20px' }}>Itens de Interesse ({interesses.length})</h3>
        
        {interesses.length === 0 ? (
          <p style={{ color: '#6b7280' }}>Este usuário ainda não adicionou nenhum item aos interesses.</p>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))', gap: '20px' }}>
            {interesses.map((item) => (
              <CartaoItem key={item.id} item={item} />
            ))}
          </div>
        )}
      </div>

    </div>
  );
}

const inputStyle = {
  width: '100%', padding: '10px', border: '1px solid #ccc', borderRadius: '6px', boxSizing: 'border-box', fontSize: '15px'
};