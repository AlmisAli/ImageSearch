// Almis Ali 300317688

package main

import (
	"fmt"
	"image"
	_ "image/jpeg"

	"log"
	"math"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"sync"
	"time"
)

type Histo struct {
	Name string
	H    []uint32
}

// adapted from: first example at pkg.go.dev/image
func computeHistogram(imagePath string, d int) (Histo, error) {
	// Open the JPEG file

	file, err := os.Open(imagePath)
	if err != nil {
		return Histo{"", nil}, err
	}
	defer file.Close()

	// Decode the JPEG image6
	img, _, err := image.Decode(file)
	if err != nil {
		return Histo{"", nil}, err
	}

	// Get the dimensions of the image
	bounds := img.Bounds()
	width, height := bounds.Max.X, bounds.Max.Y
	pixelNumber := (width * height)

	// Display RGB values for the first 5x5 pixels
	// remove y < 5 and x < 5  to scan the entire image
	histogram := make([]uint32, 512)
	rgbValues := make([][]uint32, pixelNumber)
	counter := 0

	// Extract RGB values for each pixel in the image
	for y := 0; y < height; y++ {

		for x := 0; x < width; x++ {
			// Convert the pixel to RGBA
			red, green, blue, _ := img.At(x, y).RGBA()

			// Store RGB values in the 2D slice
			red >>= 8
			blue >>= 8
			green >>= 8
			var pixel = []uint32{red, green, blue}
			rgbValues[counter] = pixel
			counter = counter + 1

		}
	}

	reducedImage := reducedPixel(rgbValues, d)
	for i := 0; i < 512; i++ {
		histogram[i] = 0
	}

	for i := 0; i < len(reducedImage); i++ {
		red := reducedImage[i][0]
		green := reducedImage[i][1]
		blue := reducedImage[i][2]

		index := ((red << (2 * d)) + (green << d) + blue) //compute histogram index
		histogram[index] = histogram[index] + 1
	}

	h := Histo{imagePath, histogram}

	return h, nil
}

func reducedPixel(rgbPixel [][]uint32, d int) [][]uint32 { // reduce pixel values to D-bit representation
	reducedPixel := make([][]uint32, len(rgbPixel))
	for i := 0; i < len(rgbPixel); i++ {
		red := rgbPixel[i][0]
		green := rgbPixel[i][1]
		blue := rgbPixel[i][2]

		reducedRed := red >> (8 - d)
		reducedGreen := green >> (8 - d)
		reducedBlue := blue >> (8 - d)
		pixel := []uint32{reducedRed, reducedGreen, reducedBlue}
		reducedPixel[i] = pixel
	}
	return reducedPixel

}

func compare(queryHistogram Histo, datasetHistogram Histo) float64 {
	var intersection float64
	var minCount float64
	intersection = 0
	normalizedQueryHisto := normalizedHistogram(queryHistogram.H)
	normalizedDatasetHisto := normalizedHistogram(datasetHistogram.H)

	for i := 0; i < len(normalizedQueryHisto); i++ {
		minCount = math.Min(float64(normalizedQueryHisto[i]), float64(normalizedDatasetHisto[i])) //compute intersection
		intersection = intersection + minCount
	}

	return intersection
}

func normalizedHistogram(histogram []uint32) []float64 {
	normalizedHistogram := make([]float64, len(histogram))
	var normalized float64
	for i := 0; i < len(histogram); i++ {
		normalized = (float64(histogram[i]) / 172800) //Divide each histogram element by number of pixels
		normalizedHistogram[i] = normalized
	}
	return normalizedHistogram

}

func computeSimilarity(fileNames []string, d int, Path string, histogramCH chan Histo, wg *sync.WaitGroup) {
	defer wg.Done()
	for i := 0; i < len(fileNames); i++ {
		imagePath := filepath.Join(Path, fileNames[i])
		histo, er := computeHistogram(imagePath, d) // compute histogram of image
		if er != nil {                              // handle Error
			log.Println("Error computing histogram for", imagePath, "-", er)
			continue
		}
		histogramCH <- histo //send histogram results
	}
}

func main() {
	start := time.Now()
	// read the directory name from command line
	args := os.Args
	directoryPathq := "..\\Database\\queryImages"
	directoryPathD := "..\\Database"
	fullPathq := filepath.Join(directoryPathq, args[1])
	d := 3
	var wg sync.WaitGroup

	queryHisto, er := computeHistogram(fullPathq, d)
	_ = er
	_ = queryHisto
	fullPathD1 := filepath.Join(directoryPathD, args[2])
	files, err := os.ReadDir(fullPathD1)
	if err != nil {
		log.Fatal(err)
	}

	// Create an array to store filenames
	var filenames []string

	// get the list of jpg files
	for _, file := range files {
		if strings.HasSuffix(file.Name(), ".jpg") {
			filenames = append(filenames, file.Name())
		}
	}

	fileListSize := len(filenames)
	k := 256
	histogramCh := make(chan Histo, 200) // Adjust buffer size as needed

	// Divide the filenames into smaller slices
	for i := 0; i < k; i++ {
		start := i * (fileListSize / k)
		end := (i + 1) * (fileListSize / k)
		if i == k-1 {
			end = fileListSize // To handle any remaining filenames
		}
		wg.Add(1)
		go computeSimilarity(filenames[start:end], d, fullPathD1, histogramCh, &wg)
	}

	go func() {
		wg.Wait()
		close(histogramCh)
	}()

	type IntersectionResult struct {
		Intersection float64
		Name         string
	}

	var closestIntersections []IntersectionResult
	//Read from histogram Channel
	for histo := range histogramCh {
		intersection := compare(queryHisto, histo)
		closestIntersections = append(closestIntersections, IntersectionResult{Intersection: intersection, Name: histo.Name})
	}

	// Sort the closestIntersections by the absolute difference from 1
	sort.Slice(closestIntersections, func(i, j int) bool {
		return math.Abs(1-closestIntersections[i].Intersection) < math.Abs(1-closestIntersections[j].Intersection)
	})

	// Print the top 5 closest intersection values to 1
	for i := 0; i < 5 && i < len(closestIntersections); i++ {
		fmt.Println(closestIntersections[i].Name, closestIntersections[i].Intersection)
	}

	end := time.Now()
	duration := end.Sub(start)

	fmt.Println(duration)

}
