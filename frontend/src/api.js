const BASE = '/api/classifica';

async function parseErrore(res) {
  const dati = await res.json().catch(() => ({}));
  if (dati.messaggio) return dati.messaggio;
  const primoErrore = Object.values(dati)[0];
  return primoErrore || 'Si è verificato un errore';
}

export async function fetchClassifica(torneoId) {
  const res = await fetch(`${BASE}/${torneoId}`);
  if (!res.ok) throw new Error(await parseErrore(res));
  return res.json();
}
