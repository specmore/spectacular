
const STORAGE_KEY = 'loginState';
const storage = window.sessionStorage;


interface LoginState {
  randomNum: number;
  returnTo: string;
}

const toEncodedString = (loginState: LoginState): string => {
  const stateString = `${loginState.randomNum}+${loginState.returnTo}`;
  return btoa(stateString);
};

const generateAndStoreLoginState = (returnTo: string): string => {
  storage.clear();

  const randomNum = Math.floor(Math.random() * 99999999) + 100000000;
  const loginState = {
    randomNum,
    returnTo,
  };

  const stateString = toEncodedString(loginState);
  storage.setItem(STORAGE_KEY, stateString);

  return stateString;
};

const isReturnedStateValid = (returnedState: string): boolean => {
  const sentState = storage.getItem(STORAGE_KEY);
  return returnedState === sentState;
};

export default {
  generateAndStoreLoginState,
  isReturnedStateValid,
};
