import axios from 'axios'

const API_HOSTNAME = "http://localhost:5000";

export const fetchCatalogueListForInstallationConfig = async (installationId, configRepo) => {
    const response = await axios.get(`${API_HOSTNAME}/api/${installationId}/${configRepo}/catalogues`);
    return response.data;
}