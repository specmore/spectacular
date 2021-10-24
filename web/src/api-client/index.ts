import axios from 'axios';

export const createFileApiURL = (
  owner: string,
  repo: string,
  fileLocation: string,
): string => `/api/catalogues/${owner}/${repo}/files/${fileLocation}`;

axios.interceptors.response.use((response) => response, (error) => {
  if (error.response.status === 401) {
    console.debug('expired token');
    console.debug(`current location:${window.location.pathname}`);

    const redirectParams = new URLSearchParams();
    redirectParams.append('backTo', window.location.pathname);

    window.location.assign(`/login/github?${redirectParams.toString()}`);
  }
  return Promise.reject(error);
});

export const fetchUserInfo: any = async () => {
  const response = await axios.get('/login/');
  return response.data;
};
