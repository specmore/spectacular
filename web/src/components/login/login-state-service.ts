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

const toLoginState = (encodedState: string): LoginState => {
  const decodedString = atob(encodedState);
  const parts = decodedString.split('+');
  const randomNum = Number(parts[0]);
  const returnTo = parts[1];
  return {
    randomNum,
    returnTo,
  };
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

const getReturnToLocation = (): string => {
  const loginState = storage.getItem(STORAGE_KEY);
  return toLoginState(loginState).returnTo;
};

export default {
  generateAndStoreLoginState,
  isReturnedStateValid,
  getReturnToLocation,
};
