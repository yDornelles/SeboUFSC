import React, { useState } from 'react';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';

export default function LoginUsuario() {
  const [formData, setFormData] = useState({
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

    try {
      // 1. Autenticar com e-mail e senha no Firebase
      const userCredential = await signInWithEmailAndPassword(
        auth,
        formData.email,
        formData.senha
      );

      setMensagem('Login realizado com sucesso!');
      setFormData({ email: '', senha: '' });

      // Aqui você pode redirecionar o usuário para a página inicial/dashboard
      console.log('Usuário autenticado:', userCredential.user);

    } catch (err) {
      // Trata os códigos de erro comuns do Firebase Auth ao realizar login
      if (err.code === 'auth/invalid-credential' || err.code === 'auth/user-not-found' || err.code === 'auth/wrong-password') {
        setErro('E-mail ou senha incorretos.');
      } else if (err.code === 'auth/invalid-email') {
        setErro('O formato do e-mail digitado é inválido.');
      } else if (err.code === 'auth/too-many-requests') {
        setErro('Muitas tentativas malsucedidas. Tente novamente mais tarde.');
      } else {
        setErro('Erro ao realizar login. Tente novamente.');
      }
    }
  };

  // Estilos compartilhados baseados na tela de cadastro
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
      maxWidth: '450px',
      margin: '0 auto',
      padding: '30px',
      backgroundColor: 'white',
      borderRadius: '12px',
      boxShadow: '0 4px 6px rgba(0,0,0,0.1)',
      fontFamily: 'sans-serif'
    }}>
      <h2 style={{ textAlign: 'center', color: '#1f2937', marginBottom: '25px' }}>
        Login - SeboUFSC
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
          placeholder="seuemail@exemplo.com"
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
          style={inputStyle}
          placeholder="Sua senha"
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
          Entrar
        </button>
      </form>
    </div>
  );
}