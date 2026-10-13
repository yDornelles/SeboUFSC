import { useCallback, useEffect, useState } from 'react';

const API = 'http://localhost:8080/api';
const INTERVALO_MS = 10000;

export default function useContadores(usuarioId) {
  const [naoLidas, setNaoLidas] = useState(0);
  const [pendentes, setPendentes] = useState(0);

  const recarregar = useCallback(async () => {
    if (!usuarioId) return;
    try {
      const [resNotif, resProp] = await Promise.all([
        fetch(`${API}/notificacoes/${usuarioId}`),
        fetch(`${API}/propostas/usuario/${usuarioId}`),
      ]);

      if (resNotif.ok) {
        const notificacoes = await resNotif.json();
        setNaoLidas(notificacoes.filter((n) => !n.lida).length);
      }

      if (resProp.ok) {
        const propostas = await resProp.json();
        const aguardandoMinhaResposta = propostas.filter(
          (p) =>
            p.status === 'AGUARDANDO_CONFIRMACAO' &&
            p.participantes.some((part) => part.id === usuarioId && !part.aceitou)
        );
        setPendentes(aguardandoMinhaResposta.length);
      }
    } catch (err) {
      console.error('Erro ao atualizar contadores', err);
    }
  }, [usuarioId]);

  useEffect(() => {
    recarregar();
    const intervalo = setInterval(recarregar, INTERVALO_MS);
    return () => clearInterval(intervalo);
  }, [recarregar]);

  return { naoLidas, pendentes, recarregar };
}
