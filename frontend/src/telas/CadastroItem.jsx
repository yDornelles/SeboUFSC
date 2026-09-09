import { useState } from 'react';
import Botao from '../componentes/Botao';

function CadastroItem() {
  // Estado inicial do formulário
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

  const [mensagem, setMensagem] = useState({ texto: '', erro: false });

  // Função para atualizar o estado do formulário conforme o usuário digita
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Função para formatar o preço antes de enviar ao backend
  const formatarPreco = () => {
    if (!formData.preco) return;

    // Substitui vírgula por ponto para conversão numérica
    let valorNumerico = parseFloat(formData.preco.replace(',', '.'));

    if (!isNaN(valorNumerico)) {
      // Formata para duas casas decimais e substitui ponto por vírgula
      let valorFormatado = valorNumerico.toFixed(2).replace('.', ',');
      setFormData({ ...formData, preco: valorFormatado });
    } else {
      setFormData({ ...formData, preco: '' }); 
    }
  };

  // Função para enviar os dados do formulário para o backend
  const cadastrar = async (e) => {
    e.preventDefault();
    setMensagem({ texto: 'Salvando...', erro: false });

    const precoFinal = formData.opcaoNegocio === 'SO_TROCA' ? 0 : parseFloat(formData.preco.replace(',', '.'));

    const payload = {
      usuarioId: "123e4567-e89b-12d3-a456-426614174000",
      ...formData,
      preco: precoFinal
    };

    try {
      const resposta = await fetch('http://localhost:8080/api/itens/cadastrar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!resposta.ok) {
        throw new Error("Erro ao salvar. Verifique se o servidor Java está rodando.");
      }

      const dados = await resposta.json();
      setMensagem({ texto: `✅ ${dados.mensagem} (ID: ${dados.id})`, erro: false });
      
      // Limpa os campos após salvar com sucesso
      setFormData({
        titulo: '', tipoMidia: '', autor: '', editora: '', status: 'DISPONIVEL',
        estadoConservacao: '', opcaoNegocio: '', preco: '', descricao: '', imagem: ''
      });

    } catch (erro) {
      setMensagem({ texto: `❌ ${erro.message}`, erro: true });
    }
  };

  // Estilos inline para inputs e labels
  const inputStyle = {
    width: '100%', padding: '10px', marginBottom: '15px', 
    border: '1px solid #ccc', borderRadius: '6px', boxSizing: 'border-box',
    fontSize: '15px'
  };
  const labelStyle = { display: 'block', fontWeight: 'bold', marginBottom: '5px', color: '#374151' };
  const asteriscoStyle = { color: '#dc2626' };

  // Renderiza o formulário de cadastro
  return (
    <div style={{ 
      maxWidth: '650px', margin: '0 auto', padding: '30px', 
      backgroundColor: 'white', borderRadius: '12px', boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
      fontFamily: 'sans-serif'
    }}>
      <h2 style={{ textAlign: 'center', color: '#1f2937', marginBottom: '25px' }}>Cadastrar Novo Item</h2>
      
      <form onSubmit={cadastrar}>
        
        <label style={labelStyle}>Título do Item <span style={asteriscoStyle}>*</span></label>
        <input name="titulo" type="text" value={formData.titulo} onChange={handleChange} required style={inputStyle} placeholder="" />

        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Autor <span style={asteriscoStyle}>*</span></label>
            <input name="autor" type="text" value={formData.autor} onChange={handleChange} required style={inputStyle} placeholder="" />
          </div>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>Editora</label>
            <input name="editora" type="text" value={formData.editora} onChange={handleChange} style={inputStyle} placeholder="" />
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
        <input name="imagem" type="text" value={formData.imagem} onChange={handleChange} style={inputStyle} placeholder="" />

        <label style={labelStyle}>Descrição <span style={asteriscoStyle}>*</span></label>
        <textarea name="descricao" value={formData.descricao} onChange={handleChange} required rows="3" style={{...inputStyle, resize: 'vertical'}} placeholder=""></textarea>

        {mensagem.texto && (
          <div style={{ 
            padding: '12px', marginBottom: '15px', borderRadius: '6px', textAlign: 'center', fontWeight: 'bold',
            backgroundColor: mensagem.erro ? '#fee2e2' : '#dcfce7',
            color: mensagem.erro ? '#991b1b' : '#166534'
          }}>
            {mensagem.texto}
          </div>
        )}

        <Botao texto="Cadastrar Item" type="submit" />
        
      </form>
    </div>
  );
}

export default CadastroItem;