import { useState, useEffect } from 'react';
import Botao from '../componentes/Botao';

function EditarItem({ usuario, itemId, onVoltar }) {
  const [formData, setFormData] = useState({
    titulo: '',
    tipoMidia: '',
    autor: '',
    editora: '',
    status: 'DISPONIVEL', 
    estadoConservacao: '',
    opcaoNegocio: '',
    preco: '',
    descricao: '',
    imagem: ''
  });

  const [mensagem, setMensagem] = useState({ texto: 'Carregando dados do item...', erro: false });

  useEffect(() => {
    const carregarItem = async () => {
      if (!itemId) return;
      
      try {
        const resposta = await fetch(`http://localhost:8080/api/itens/${itemId}`);
        
        if (!resposta.ok) {
          throw new Error("Erro ao buscar os dados do item.");
        }

        const dados = await resposta.json();
        
        let precoFormatado = '';
        if (dados.preco !== undefined && dados.preco !== null) {
          precoFormatado = Number(dados.preco).toFixed(2).replace('.', ',');
        }

        setFormData({
          titulo: dados.titulo || '',
          tipoMidia: dados.tipoMidia || '',
          autor: dados.autor || '',
          editora: dados.editora || '',
          status: dados.status || 'DISPONIVEL',
          estadoConservacao: dados.estadoConservacao || '',
          opcaoNegocio: dados.opcaoNegocio || '',
          preco: precoFormatado,
          descricao: dados.descricao || '',
          imagem: dados.imagem || ''
        });
        
        setMensagem({ texto: '', erro: false }); 

      } catch (erro) {
        setMensagem({ texto: `❌ ${erro.message}`, erro: true });
      }
    };

    carregarItem();
  }, [itemId]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const formatarPreco = () => {
    if (!formData.preco) return;
    let valorNumerico = parseFloat(String(formData.preco).replace(',', '.'));
    if (!isNaN(valorNumerico)) {
      let valorFormatado = valorNumerico.toFixed(2).replace('.', ',');
      setFormData({ ...formData, preco: valorFormatado });
    } else {
      setFormData({ ...formData, preco: '' }); 
    }
  };

  const salvarEdicao = async (e) => {
    e.preventDefault();
    setMensagem({ texto: 'Salvando alterações...', erro: false });

    const idUsuarioAtual = usuario?.uid || usuario?.id;
    if (!idUsuarioAtual) {
      setMensagem({ texto: '❌ Erro: Usuário não autenticado.', erro: true });
      return;
    }

    const precoFinal = formData.opcaoNegocio === 'SO_TROCA' ? 0 : parseFloat(String(formData.preco).replace(',', '.'));

    const payload = {
      usuarioId: idUsuarioAtual, 
      ...formData,
      preco: precoFinal
    };

    try {
      const resposta = await fetch(`http://localhost:8080/api/itens/editar/${itemId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!resposta.ok) {
        throw new Error("Erro ao atualizar. Verifique se o servidor Java está rodando.");
      }

      const dados = await resposta.json();
      setMensagem({ texto: `✅ ${dados.mensagem}`, erro: false });

      setTimeout(() => {
        onVoltar();
      }, 1500);

    } catch (erro) {
      setMensagem({ texto: `❌ ${erro.message}`, erro: true });
    }
  };

  const inputStyle = { width: '100%', padding: '10px', marginBottom: '15px', border: '1px solid #ccc', borderRadius: '6px', boxSizing: 'border-box', fontSize: '15px' };
  const labelStyle = { display: 'block', fontWeight: 'bold', marginBottom: '5px', color: '#374151' };
  const asteriscoStyle = { color: '#dc2626' };

  return (
    <div style={{ maxWidth: '650px', margin: '0 auto', padding: '30px', backgroundColor: 'white', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)', fontFamily: 'sans-serif' }}>
      <h2 style={{ textAlign: 'center', color: '#1f2937', marginBottom: '25px' }}>Editar Item</h2>
      
      <form onSubmit={salvarEdicao}>
        
        <label style={labelStyle}>Título do Item <span style={asteriscoStyle}>*</span></label>
        <input name="titulo" type="text" value={formData.titulo} onChange={handleChange} required style={inputStyle} />

        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Autor <span style={asteriscoStyle}>*</span></label>
            <input name="autor" type="text" value={formData.autor} onChange={handleChange} required style={inputStyle} />
          </div>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Editora</label>
            <input name="editora" type="text" value={formData.editora} onChange={handleChange} style={inputStyle} />
          </div>
        </div>

        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Tipo de Mídia <span style={asteriscoStyle}>*</span></label>
            <select name="tipoMidia" value={formData.tipoMidia} onChange={handleChange} style={inputStyle}>
              <option value="" disabled>Selecione uma opção...</option>
              <option value="LIVRO">Livro</option>
              <option value="HQ">HQ</option>
              <option value="MANGA">Mangá</option>
              <option value="REVISTA">Revista</option>
              <option value="CD">CD</option>
              <option value="DVD">DVD</option>
              <option value="VINIL">Vinil</option>
            </select>
          </div>
          
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Conservação <span style={asteriscoStyle}>*</span></label>
            <select name="estadoConservacao" value={formData.estadoConservacao} onChange={handleChange} style={inputStyle}>
              <option value="" disabled>Selecione uma opção...</option>
              <option value="NOVO">Novo</option>
              <option value="SEMI_NOVO">Seminovo</option>
              <option value="USADO">Usado</option>
              <option value="DANIFICADO">Danificado</option>
            </select>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Opção de Negócio <span style={asteriscoStyle}>*</span></label>
            <select name="opcaoNegocio" value={formData.opcaoNegocio} onChange={handleChange} style={inputStyle}>
              <option value="" disabled>Selecione uma opção...</option>
              <option value="SO_VENDA">Só Venda</option>
              <option value="SO_TROCA">Só Troca</option>
              <option value="TROCA_E_VENDA">Troca e Venda</option>
            </select>
          </div>
          
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Preço (R$) <span style={asteriscoStyle}>*</span></label>
            <input 
              name="preco" type="text" inputMode="decimal" step="0.01" min="0"
              value={formData.opcaoNegocio === 'SO_TROCA' ? '0,00' : formData.preco} 
              onChange={(e) => {
                const valorLimpo = e.target.value.replace(/[^0-9.,]/g, '');
                setFormData({ ...formData, preco: valorLimpo });
              }}
              onBlur={formatarPreco}
              required={formData.opcaoNegocio !== 'SO_TROCA'} 
              disabled={formData.opcaoNegocio === 'SO_TROCA'}
              style={{
                ...inputStyle, 
                backgroundColor: formData.opcaoNegocio === 'SO_TROCA' ? '#f3f4f6' : 'white'
              }} 
              placeholder="0,00" 
            />
          </div>
        </div>

        <label style={labelStyle}>URL da Imagem <span style={asteriscoStyle}>*</span></label>
        <input name="imagem" type="text" value={formData.imagem} onChange={handleChange} style={inputStyle} />

        <label style={labelStyle}>Descrição <span style={asteriscoStyle}>*</span></label>
        <textarea name="descricao" value={formData.descricao} onChange={handleChange} required rows="3" style={{...inputStyle, resize: 'vertical'}}></textarea>

{mensagem.texto && (
          <div style={{ 
            padding: '12px', marginBottom: '15px', borderRadius: '6px', textAlign: 'center', fontWeight: 'bold',
            backgroundColor: mensagem.erro ? '#fee2e2' : '#dcfce7',
            color: mensagem.erro ? '#991b1b' : '#166534'
          }}>
            {mensagem.texto}
          </div>
        )}

        <div style={{ display: 'flex', gap: '15px', marginTop: '20px' }}>
          <button 
            type="submit" 
            style={{ 
              flex: 1, 
              backgroundColor: '#1d4ed8', 
              color: 'white', 
              border: 'none', 
              padding: '12px', 
              borderRadius: '6px', 
              cursor: 'pointer', 
              fontWeight: 'bold', 
              fontSize: '16px' 
            }}
          >
            Salvar Alterações
          </button>
          
          <button 
            type="button" 
            onClick={onVoltar} 
            style={{ 
              flex: 1, 
              backgroundColor: '#6b7280', 
              color: 'white', 
              border: 'none', 
              padding: '12px', 
              borderRadius: '6px', 
              cursor: 'pointer', 
              fontWeight: 'bold', 
              fontSize: '16px' 
            }}
          >
            Cancelar Edição
          </button>
        </div>
        
      </form>
    </div>
  );
}

export default EditarItem;