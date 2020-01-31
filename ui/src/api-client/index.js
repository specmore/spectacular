import axios from 'axios'

//const API_HOSTNAME = "http://localhost:5000";

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