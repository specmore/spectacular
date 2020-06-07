import { SpecLog } from '../../backend-api-client';
import SpecItem from './spec-item';

const generateSpecLog = ({
  interfaceName = 'testInterface1',
  latestAgreed = SpecItem.generateSpecItem(),
  proposedChanges = [],
}: Partial<SpecLog> = {}): SpecLog => ({
  interfaceName,
  latestAgreed,
  proposedChanges,
});

export default {
  generateSpecLog,
};
