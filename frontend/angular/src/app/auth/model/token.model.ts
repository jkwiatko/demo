export class TokenModel {
  constructor(public token: string, public expirationTimestamp: number) {
  }

  hasNotExpired(): boolean {
    return this.expirationDuration() > 0;
  }

  expirationDuration(): number {
    return new Date(this.expirationTimestamp).getTime() - new Date().getTime();
  }
}
