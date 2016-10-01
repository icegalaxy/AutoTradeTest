package icegalaxy.net;

import java.util.ArrayList;

public class RSI {

	public RSI(ArrayList<Float> close, int noOfPeriods) {

		this.noOfPeriods = noOfPeriods;
		this.close = close; // passing an
		// object�A��Global.close�ܪ��ܩO���ܭ��ܡH�̦n�ܡA�]�����ܪ���getRSI()�|����
		// �p�G�u�Y�������ܡA�i�H�YTIThread�����A�Ncreate Object�G�y��f�J��loop

	}
	
	private void calculateFirstAG() {

		if (close.size() < noOfPeriods + 2) {
			firstAverageGain = 1;
			firstAverageLoss = 1;
			return;
		}
		float totalGain = 0;
		float totalLoss = 0;

		for (int i = noOfPeriods; i > 0; i--) { // �̫�|loop��h
			// i=1�A�I�ѭ��ni=0�Y�]���G�Ӽƥu�Y�ξ���
			// i=1�ӼơA
			// �Y��if�G�׳̫�|�Ψ�A�p�G���Y�|�ܥ�get(-1)
			// First AG�Y�n�Q����data���p�쪺

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				totalGain += different; // ���e�~�M�p���ơA�����[��close.get(i)���htotalGain�סA���������ȡAshit!
			else if (different < 0)
				totalLoss += Math.abs(different); // �n����

			// �۴��s���ܴN���~�z�A���~�[�A�S���LAG or AL
		}

		firstAverageGain = totalGain / noOfPeriods;
		firstAverageLoss = totalLoss / noOfPeriods;
	}

	private void calculateAG() {
		
		if (close.size() < noOfPeriods + 2) {
			averageGain = 1;
			averageLoss = 1;
			return;
		}

		float temAG = firstAverageGain;
		float temAL = firstAverageLoss;

		for (int i = noOfPeriods + 1; i < close.size(); i++) {  //�n�p�ͥ��p��previous ag/av

			float gain = 0; // �Y��initial�U���i�H���֦n�h��
			float loss = 0;

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				gain = different;
			else if (different < 0)
				loss = Math.abs(different);
			// �۴��s���~�z�Again and loss�|����0

			temAG = (temAG * (noOfPeriods - 1) + gain) / noOfPeriods; //�ۤv�gLoop
			temAL = (temAL * (noOfPeriods - 1) + loss) / noOfPeriods;
		}

		averageGain = temAG;
		averageLoss = temAL;

	}
	
	// previous RSI, ������
	public float getRSI() {

		calculateFirstAG();
		calculateAG();

		if (averageLoss == 0) // �קK���s
			return 100;

		float f = 100 - 100 / (1 + averageGain / averageLoss);
		// System.out.println("CurrentRSI: " + f);
		return f;
	}

	// current RSI,�Ӯa����A�ҥH�ntake currentPoint as para
	public float getRSI(float currentPoint) {

		if (close.size() <= noOfPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float currentGain = 0;
		float currentLoss = 0;

		if ((currentPoint - close.get(close.size() - 1)) >= 0) // �P�̫��record���
			currentGain = currentPoint - close.get(close.size()-1);
		else
			currentLoss = currentPoint - close.get(close.size()-1);

		float currentAverageGain = ((averageGain * (noOfPeriods - 1)) + currentGain)
				/ noOfPeriods;
		float currentAverageLoss = ((averageLoss * (noOfPeriods - 1)) + currentLoss)
				/ noOfPeriods;

		float f = 100 - 100 / (1 + currentAverageGain / currentAverageLoss);
		return f;

	}

	public float getAverageGain() {
		return averageGain;
	}

	public float getAverageLoss() {
		return averageLoss;
	}
	
	public float getALAGsoFar() {
		
		if (close.size() < 2) {
			return 0;
		}
				

		float temAG = 0;
		float temAL = 0;

		for (int i = 1; i < close.size(); i++) {

			float gain = 0; // �Y��initial�U���i�H���֦n�h��
			float loss = 0;

			float different = close.get(i) - close.get(i - 1);

			if (different > 0)
				gain = different;
			else if (different < 0)
				loss = Math.abs(different);
			// �۴��s���~�z�Again and loss�|����0

			temAG = (temAG * (close.size() - 1) + gain) / close.size();
			temAL = (temAL * (close.size() - 1) + loss) / close.size();
		}

		return temAG + temAL;

	}

	int noOfPeriods;
	ArrayList<Float> close = new ArrayList<Float>();
	float firstAverageGain;
	float firstAverageLoss;
	float averageGain;
	float averageLoss;
	

}
