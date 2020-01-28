import axios from 'axios'

//const API_HOSTNAME = "http://localhost:5000";

export const fetchCatalogueListForInstallationConfig = async (installationId, configRepo) => {
    const response = await axios.get(`/api/${installationId}/${configRepo}/catalogues`);
    return response.data;
}

export const fetchInstances = async (installationId, configRepo) => {
    const response = await axios.get(`/api/${installationId}/${configRepo}/catalogues`);
    return response.data;
}

export const fetchUserInfo = async () => {
    const response = await axios.get(`/login`);
    return response.data;
}