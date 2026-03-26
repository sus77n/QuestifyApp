export interface OptionDTO {
  id: string;
  header?: string;
  text: string;
  side?: string;
  metadata: MetaData
}

export interface MetaData {
  side?: string
}