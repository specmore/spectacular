import axios from 'axios'

axios.interceptors.response.use((response) => { return response; }, (error) => {
    if (error.response.status === 401) {
        console.log("expired token");
        console.log("current location:" + window.location.pathname);

        const redirectParams = new URLSearchParams();
        redirectParams.append('backTo', window.location.pathname);

        window.location.assign('/login/github?'+redirectParams.toString());
    }
    return Promise.reject(error);
  });

export const fetchCatalogues = async (org) => {
    const response = await axios.get(`/api/${org}/catalogues`);
    return response.data;
}

export const fetchInstallation = async () => {
    const response = await axios.get(`/api/installation`);
    return response.data;
}

export const fetchInstances = async () => {
    const response = await axios.get(`/api/instances`);
    return response.data;
}

export const fetchUserInfo = async () => {
    const response = await axios.get(`/login`);
    return response.data;
}