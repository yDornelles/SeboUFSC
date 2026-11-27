import React, { useState } from 'react';
import { createUserWithEmailAndPassword, deleteUser } from 'firebase/auth';
import { auth } from '../firebase';

export default function CadastroUsuario() {
  const [formData, setFormData] = useState({
    nome: '',
    sobrenome: '',
    nomeUsuario: '',
    email: '',
    senha: ''
  });

  const [mensagem, setMensagem] = useState('');
  const [erro, setErro] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensagem('');
    setErro('');

    // Validação 1: Impede espaços em branco no nome de usuário
    if (/\s/.test(formData.nomeUsuario)) {
      setErro('O nome de usuário não pode conter espaços em branco.');
      return;
    }

    // Validação 2: Tamanho mínimo
    if (formData.nomeUsuario.trim().length < 6) {
      setErro('O nome de usuário deve ter no mínimo 6 caracteres.');
      return;
    }

    let userCredential = null;

    try {
      userCredential = await createUserWithEmailAndPassword(
        auth, 
        formData.email, 
        formData.senha
      );

      const firebaseUid = userCredential.user.uid;

      const response = await fetch('http://localhost:8080/api/usuarios/cadastrar', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: firebaseUid,
          nome: formData.nome,
          sobrenome: formData.sobrenome,
          nomeUsuario: formData.nomeUsuario,
          email: formData.email
        })
      });

      if (!response.ok) {
        const responseText = await response.text();
        let errorMessage = 'Erro ao cadastrar usuário no servidor.';

        try {
          const errorJson = JSON.parse(responseText);
          errorMessage = errorJson.mensagem || errorJson.message || responseText;
        } catch {
          if (responseText) errorMessage = responseText;
        }

        throw new Error(errorMessage);
      }

      const data = await response.json();
      setMensagem(`${data.mensagem || 'Usuário cadastrado com sucesso!'} (@${formData.nomeUsuario})`);
      setFormData({ nome: '', sobrenome: '', nomeUsuario: '', email: '', senha: '' });

    } catch (err) {
      if (userCredential && userCredential.user) {
        try {
          await deleteUser(userCredential.user);
        } catch (deleteErr) {
          console.error('Erro ao reverter cadastro no Firebase:', deleteErr);
        }
      }

      if (err.code === 'auth/invalid-email') {
        setErro('O formato do e-mail digitado é inválido.');
      } else if (err.code === 'auth/email-already-in-use') {
        setErro('Este e-mail já está sendo utilizado por outra conta.');
      } else if (err.code === 'auth/weak-password') {
        setErro('A senha deve ter pelo menos 6 caracteres.');
      } else {
        setErro(err.message);
      }
    }
  };

  // Estilos compartilhados baseados no CadastroItem
  const inputStyle = {
    width: '100%',
    padding: '10px',
    marginBottom: '15px',
    border: '1px solid #ccc',
    borderRadius: '6px',
    boxSizing: 'border-box',
    fontSize: '15px'
  };

  const labelStyle = {
    display: 'block',
    fontWeight: 'bold',
    marginBottom: '5px',
    color: '#374151'
  };

  const asteriscoStyle = { color: '#dc2626' };

  return (
    <div style={{
      maxWidth: '650px',
      margin: '0 auto',
      padding: '30px',
      backgroundColor: 'white',
      borderRadius: '12px',
      boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
      fontFamily: 'sans-serif'
    }}>
      <h2 style={{ textAlign: 'center', color: '#1f2937', marginBottom: '25px' }}>
        Cadastro de Usuário
      </h2>

      {mensagem && (
        <div style={{
          padding: '12px',
          marginBottom: '15px',
          borderRadius: '6px',
          textAlign: 'center',
          fontWeight: 'bold',
          backgroundColor: '#dcfce7',
          color: '#166534'
        }}>
          ✅ {mensagem}
        </div>
      )}

      {erro && (
        <div style={{
          padding: '12px',
          marginBottom: '15px',
          borderRadius: '6px',
          textAlign: 'center',
          fontWeight: 'bold',
          backgroundColor: '#fee2e2',
          color: '#991b1b'
        }}>
          ❌ {erro}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>
              Nome <span style={asteriscoStyle}>*</span>
            </label>
            <input
              type="text"
              name="nome"
              value={formData.nome}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </div>

          <div style={{ flex: '1 1 200px' }}>
            <label style={labelStyle}>
              Sobrenome <span style={asteriscoStyle}>*</span>
            </label>
            <input
              type="text"
              name="sobrenome"
              value={formData.sobrenome}
              onChange={handleChange}
              required
              style={inputStyle}
            />
          </div>
        </div>

        <label style={labelStyle}>
          Nome de Usuário (@) <span style={asteriscoStyle}>*</span>
        </label>
        <input
          type="text"
          name="nomeUsuario"
          value={formData.nomeUsuario}
          onChange={handleChange}
          required
          minLength={6}
          maxLength={20}
          style={inputStyle}
        />

        <label style={labelStyle}>
          E-mail <span style={asteriscoStyle}>*</span>
        </label>
        <input
          type="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
          style={inputStyle}
        />

        <label style={labelStyle}>
          Senha <span style={asteriscoStyle}>*</span>
        </label>
        <input
          type="password"
          name="senha"
          value={formData.senha}
          onChange={handleChange}
          required
          minLength={6}
          style={inputStyle}
        />

        <button
          type="submit"
          style={{
            backgroundColor: '#1d4ed8',
            color: 'white',
            padding: '12px',
            border: 'none',
            borderRadius: '8px',
            fontSize: '16px',
            fontWeight: 'bold',
            cursor: 'pointer',
            width: '100%',
            marginTop: '10px',
            transition: 'background-color 0.2s'
          }}
          onMouseOver={(e) => e.target.style.backgroundColor = '#1e40af'}
          onMouseOut={(e) => e.target.style.backgroundColor = '#1d4ed8'}
        >
          Cadastrar Usuário
        </button>
      </form>
    </div>
  );
}