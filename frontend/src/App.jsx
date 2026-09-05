import { useState } from 'react';

function App() {
  // Estados para guardar os valores digitados e a resposta do Java
  const [valorA, setValorA] = useState('');
  const [valorB, setValorB] = useState('');
  const [resultado, setResultado] = useState('...');

  const enviarParaJava = async () => {
    if (valorA === '' || valorB === '') {
      alert("Digite os dois números!");
      return;
    }

    // O payload tem as exatas mesmas chaves do seu AdicaoRequest.java
    const payload = {
      valorA: parseFloat(valorA),
      valorB: parseFloat(valorB)
    };

    try {
      // Faz o POST para o Controller do Spring Boot
      const resposta = await fetch('http://localhost:8080/api/calculadora/somar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      if (!resposta.ok) {
        throw new Error("Erro na comunicação com o backend");
      }

      const dados = await resposta.json();
      setResultado(dados.resultado); // Atualiza a tela com o AdicaoResponse

    } catch (erro) {
      console.error(erro);
      setResultado("Erro: Verifique se o Java está rodando!");
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h2>Calculadora do Sebo (React Version)</h2>
      
      <input 
        type="number" 
        placeholder="Número 1" 
        value={valorA} 
        onChange={(e) => setValorA(e.target.value)} 
        style={{ display: 'block', marginBottom: '10px' }}
      />
      
      <input 
        type="number" 
        placeholder="Número 2" 
        value={valorB} 
        onChange={(e) => setValorB(e.target.value)} 
        style={{ display: 'block', marginBottom: '15px' }}
      />
      
      <button onClick={enviarParaJava}>Calcular no Backend</button>

      <h3 style={{ color: '#2563eb' }}>Resultado: {resultado}</h3>
    </div>
  );
}

export default App;