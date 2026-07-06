import { useEffect, useState } from 'react';
import { fetchClassifica } from './api.js';

export default function ClassificaWidget({ torneoId }) {
  const [classifica, setClassifica] = useState([]);
  const [caricamento, setCaricamento] = useState(true);

  useEffect(() => {
    fetchClassifica(torneoId)
      .then((dati) => setClassifica(dati))
      .catch((err) => console.error('Errore nel caricamento:', err))
      .finally(() => setCaricamento(false));
  }, [torneoId]);

  if (caricamento) return <p>Caricamento in corso...</p>;

  return (
    <ul>
      {classifica.map((riga, i) => (
        <li key={i}>{JSON.stringify(riga)}</li>
      ))}
    </ul>
  );
}
