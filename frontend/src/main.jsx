import { createRoot } from 'react-dom/client';
import ClassificaWidget from './ClassificaWidget.jsx';

const el = document.getElementById('classifica-root');
if (el) {
  const torneoId = el.dataset.torneoId;
  createRoot(el).render(<ClassificaWidget torneoId={torneoId} />);
}
