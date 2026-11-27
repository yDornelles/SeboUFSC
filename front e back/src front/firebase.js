import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

// Sua configuração do Firebase
const firebaseConfig = {
  apiKey: "AIzaSyAfO0MSJwLJ7PHrlTRZ8LHIJtwu_br93L0",
  authDomain: "seboufsc.firebaseapp.com",
  projectId: "seboufsc",
  storageBucket: "seboufsc.firebasestorage.app",
  messagingSenderId: "490177208490",
  appId: "1:490177208490:web:f8d38456bf26bdd2ad30d3",
  measurementId: "G-YEZWBY2ERS"
};

// Inicializa o Firebase
const app = initializeApp(firebaseConfig);

// Exporta o serviço de autenticação para uso na aplicação
export const auth = getAuth(app);