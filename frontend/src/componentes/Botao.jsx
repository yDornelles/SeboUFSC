function Botao({ texto, type = "button" }) {
  return (
    <button 
      type={type} 
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
      {texto}
    </button>
  );
}

export default Botao;